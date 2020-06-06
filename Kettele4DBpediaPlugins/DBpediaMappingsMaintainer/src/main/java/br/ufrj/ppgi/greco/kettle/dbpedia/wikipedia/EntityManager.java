package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class EntityManager {

	/**
	 * Obter um objeto Entity a partir do JSon
	 * @param json
	 * @return
	 */
	public  static Entity popularEntity(String json, String [] propertiesForComparison){
		Entity entity=null;
		
		JSONObject parser = new JSONObject(json);
		
		JSONObject articleKey=parser.getJSONObject("article");
		
		String title = articleKey.getString("title");
		
		JSONObject infoboxKey=articleKey.getJSONObject("infobox");
		
		JSONArray propertiesvalues = infoboxKey.getJSONArray("propertiesvalues");
		
		int length=propertiesvalues.length();
		
		HashMap<String, String> propertiesValuesHashM= new HashMap<String, String>();
		
		for(int i=0;i<length; i++){
			
			JSONObject propertyValueKey = propertiesvalues.getJSONObject(i);
			
			String property = propertyValueKey.getString("property");
			
			String value = propertyValueKey.getString("value");
			
			//Armazenar.
		    propertiesValuesHashM.put(property, value);
			 
		}
	
	    entity = new Entity();
	    
	    entity.setTitle(title);
	    //entity.setInfoboxName(infoboxName);
	    entity.setPropertiesValuesHaspMap(propertiesValuesHashM);
	    entity.setPropertiesForComparison(propertiesForComparison);
	    
		
		return entity;
	}
}
