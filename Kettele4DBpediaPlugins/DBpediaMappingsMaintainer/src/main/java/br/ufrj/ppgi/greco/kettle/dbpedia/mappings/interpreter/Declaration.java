package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import java.util.HashMap;
import java.util.List;

public abstract class Declaration implements IDeclaration{

	//tipo de declaração
    String term;
    
    //Para armazenar os parâmetros
    protected HashMap<String, Object> parametersValues;
	
	
	public String getTerm(){
		return term;
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
	
	
}
