package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Condition;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class ConditionDeclaration extends Declaration {

	
	private HashMap<String, Object> parametersValues;
	
	public ConditionDeclaration(String term){
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
				@SuppressWarnings("unchecked")
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
	
	/**
	 * Retorna uma condition
	 */
	@Override
	public Object interpret(Template template){
		
		if(template==null){
			return null;
		}
		
		//System.out.println(" ConditionDeclaration EXECUTOU OK...");
		
		Condition condition=null;
			
		String templateProperty=(String) parametersValues.get("templateProperty");
		String operator=(String) parametersValues.get("operator");
		String value=(String) parametersValues.get("value");
			
		@SuppressWarnings("unchecked")
		List<Declaration>  mapping=(List<Declaration>) 
					parametersValues.get("mapping");
			
		String mapToClass=null;
			
		for (Declaration  declaration:  mapping){
				
			Object obj=declaration.interpret(template);
				
			if (obj != null && obj instanceof TemplateMapping){
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
	/**
	 * @param template
	 * @param templateMapping
	 * @return 
	 */
	public TemplateMapping generateConditions(Template template, TemplateMapping templateMapping) {
		
		@SuppressWarnings("unchecked")
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
	
}
