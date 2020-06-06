package br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;


public class EntityManager {

	/**
	 *  * Obter um objeto Entity a partir do JSon
	 * @param title
	 * @param infoboxContentJson
	 * @param infoboxContentJSON2 
	 * @param propertiesForComparison
	 * @return
	 */
	
	public  Entity popularEntity(String title, String infoboxTitle, String infoboxContentJson,  String [] propertiesForComparison){
		Entity entity=null;
		
		JSONObject parser = new JSONObject(infoboxContentJson);
		
		JSONObject infoboxKey=parser.getJSONObject("infobox");
		
		JSONArray propertiesvalues = infoboxKey.getJSONArray("propertiesvalues");
		
		int length=propertiesvalues.length();
		
		HashMap<String, String> propertiesValuesHashM= new HashMap<String, String>();
		
		for(int i=0;i<length; i++){
			
			JSONObject propertyValueKey = propertiesvalues.getJSONObject(i);
			
			String property = propertyValueKey.getString("property").trim();
			
			String value = propertyValueKey.getString("value").trim();
			
			//Armazenar.
		    propertiesValuesHashM.put(property, value);
		}
	
	    entity = new Entity(propertiesForComparison);
	    
	    entity.setTitle(title);
	    //entity.setInfoboxName(infoboxName);
	    entity.setPropertiesValuesHaspMap(propertiesValuesHashM);
	    //entity.setPropertiesForComparison(propertiesForComparison);
	    
		return entity;
	}
}
