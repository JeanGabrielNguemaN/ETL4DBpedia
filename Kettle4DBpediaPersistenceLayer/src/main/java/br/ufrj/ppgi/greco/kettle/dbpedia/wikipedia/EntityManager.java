/*
* %W% %E% Jean Gabriel Nguema Ngomo
*
* Copyright 2021 Jean Gabriel Nguema Ngomo
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

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
