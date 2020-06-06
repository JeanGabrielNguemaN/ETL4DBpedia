package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;


import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.PropertyMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class PropertyMappingDeclaration  extends Declaration{
	
//private HashMap<String, Object> parametersValues;
	
	public PropertyMappingDeclaration(String term){
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
		//System.out.println("}}");
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
		
	
		if(template==null){
			return null;
		}
	
		//recuperar
		String templateProperty=(String) parametersValues.get("templateProperty");
		String ontologyProperty=(String) parametersValues.get("ontologyProperty");
		String unit=(String) parametersValues.get("unit");
			
		PropertyMapping propertyMapping = 
				new PropertyMapping(templateProperty,ontologyProperty, unit);
			
		return propertyMapping;
	}
	

}
