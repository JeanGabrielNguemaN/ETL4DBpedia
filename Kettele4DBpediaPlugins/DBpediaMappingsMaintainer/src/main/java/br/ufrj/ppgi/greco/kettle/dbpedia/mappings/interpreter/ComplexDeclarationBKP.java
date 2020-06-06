package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.PropertyMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class ComplexDeclarationBKP extends Declaration {

	private HashMap<String, Object> parametersValues;
	
	public ComplexDeclarationBKP(String term){
		this.term=term;
		
		 parametersValues= new HashMap<String, Object>(); 
	}
	public void print(){
		
		Set<String> params=parametersValues.keySet();
		
		System.out.println("{{"+this.term);
		
		for (String  param:  params){
			Object value = parametersValues.get(param);
			
			System.out.print(" | "+param+ " = ");
			//se String print
			if (value instanceof String){
				System.out.println(""+value);
				continue;
			}
			
			if(value instanceof List<?>){
				List<Declaration> declarations = (List<Declaration>)value;
				for (Declaration  declaration:  declarations){
					declaration.print();
				}
							
			}
			
		}
		System.out.println("}}");
	}
	
	public boolean add(String parameter, Object obj){
		
		//apenas String ou List<Declaration>
		if (obj instanceof String){
			
			parametersValues.put(parameter, (String) obj) ;
			return true;
		}
		if (obj instanceof List<?>){
			
			parametersValues.put(parameter, obj) ;
			return true;
		}
		return false;
	}
	@Override
	public Object interpret(Template template){
		
		//System.out.println("Term ="+ this.term);
		if(this.getTerm().equals("TemplateMapping")){
			
			TemplateMapping templateMapping= new TemplateMapping(template.getId());
			
			//Set<String> params=parametersValues.keySet();
			
			//existe no minimo uma classe
			String mapToClass=(String) parametersValues.get("mapToClass");
			
			ArrayList<String> mappedClasses= new ArrayList<String>();
			
			mappedClasses.add(mapToClass);
			
			templateMapping.setMappedClasses(mappedClasses);
			
			System.out.println("mapToClass ="+mapToClass );
			
			System.out.println("SIZE parametersValues  ="+parametersValues.size() );
			//possui mappings?
			if(parametersValues.size()==2){
				
				templateMapping=generatePropertiesMapping(template, templateMapping, "mappings");
				
			}
			return templateMapping;
		}
		
		
		if(this.term.equals("ConditionalMapping")){
			TemplateMapping templateMapping= new TemplateMapping(template.getId());
			
			templateMapping.setConditionalMapping(true);
			
			//Tratar conditions
			templateMapping=generateConditions(template, templateMapping);
		
		   //Tratar mapping de propriedades
			templateMapping=generatePropertiesMapping(template, templateMapping, "defaultMappings");
			
			return templateMapping;
		}
		
		
		if(this.term.equals("PropertyMapping")){
			
			//recuperar
			String templateProperty=(String) parametersValues.get("templateProperty");
			String ontologyProperty=(String) parametersValues.get("ontologyProperty");
			String unit=(String) parametersValues.get("unit");
			
			PropertyMapping propertyMapping = new PropertyMapping(templateProperty,ontologyProperty, unit);
			
			return propertyMapping;
		}
		
		if(this.term.equals("Condition")){
			
			Condition condition=null;
			
			String templateProperty=(String) parametersValues.get("templateProperty");
			String operator=(String) parametersValues.get("operator");
			String value=(String) parametersValues.get("value");
			
			List<Declaration>  mapping=(List<Declaration>) 
					parametersValues.get("mapping");
			
			String mapToClass=null;
			
			for (Declaration  declaration:  mapping){
				
				Object obj=declaration.interpret(template);
				
				if (obj != null && obj instanceof TemplateMapping){
					//System.out.println(""+obj);
					TemplateMapping tmpMapping = (TemplateMapping)obj;
					ArrayList<String> mappedClasses = tmpMapping.getMappedClasses();
					mapToClass = mappedClasses.get(0);
					//so tem uma
					//break;
				}
					
			}
			
			//nova condiçao
			condition= new Condition(templateProperty, operator, 
					value, mapToClass);
			
			
			return condition;
		}
		
		
		
	 return null;
		
	}
	/**
	 * @param template
	 * @param templateMapping
	 * @return 
	 */
	public TemplateMapping generateConditions(Template template, TemplateMapping templateMapping) {
		
		List<Declaration> conditionsDeclarations=
				(List<Declaration>)parametersValues.get("cases");
		
		if(conditionsDeclarations==null){
			return templateMapping;
		}
		ArrayList<Condition> conditions= new ArrayList<Condition>();
		
		for (Declaration  declaration:  conditionsDeclarations){
			
			Object obj=declaration.interpret(template);
			
			if (obj != null && obj instanceof Condition){
				
				Condition condition = (Condition)obj;
				
				conditions.add(condition);
			}		
		} //for
		templateMapping.setConditions(conditions);
		
		return templateMapping;
	}
	/**
	 * @param template
	 * @param templateMapping
	 * @param atributesMappings
	 * @param declarations
	 * @return 
	 */
	public TemplateMapping generatePropertiesMapping(Template template, TemplateMapping templateMapping, String parameter) {
		
		//defaultMappings
		List<Declaration> declarations=(List<Declaration>)parametersValues.get(parameter);
		
		
		if(declarations==null){
			return templateMapping;
		}
		
		HashMap<String,String> atributesMappings= new HashMap<String,String>();
		
			
		for (Declaration  declaration:  declarations){
				
				Object obj=declaration.interpret(template);
				
				if (obj != null && obj instanceof PropertyMapping){
					
					PropertyMapping propertyMapping = (PropertyMapping)obj;
					
					//adicionar Mapeamento
					atributesMappings.put(propertyMapping.getTemplateProperty(), 
							propertyMapping.getOntologyProperty());
				}
					
			templateMapping.setAtributesMappings(atributesMappings);
			
			
		} //for
		return templateMapping;
		
		
	}
}
