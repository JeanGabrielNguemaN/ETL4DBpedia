package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.PropertyMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class ComplexDeclaration extends Declaration {

	private HashMap<String, Object> parametersValues;
	
	public ComplexDeclaration(String term){
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
		
	 return "";
		
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
