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
package br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia;

import java.util.HashMap;


/**
 * Usada para a comparação dos artigos na Wikipedia
 * @author Jean Gabriel Nguema Ngomo
 *
 */
public class Entity {
	private String title;
	private String infoboxName;
	private HashMap<String, String> propertiesValuesHaspMap;
	private String[] propertiesForComparison=null;
	
	//constructors
	public Entity() {
	}
	
	public Entity(String[] propertiesForComparison) {
		this();
		this.propertiesForComparison= propertiesForComparison;
	}
	
	//setters and getters
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInfoboxName() {
		return infoboxName;
	}
	public void setInfoboxName(String infoboxName) {
		this.infoboxName = infoboxName;
	}
	public HashMap<String, String> getPropertiesValuesHaspMap() {
		return propertiesValuesHaspMap;
	}
	public void setPropertiesValuesHaspMap(HashMap<String, String> propertiesValuesHaspMap) {
		this.propertiesValuesHaspMap = propertiesValuesHaspMap;
	}
	public String[] getPropertiesForComparison() {
		return propertiesForComparison;
	}
	public void setPropertiesForComparison(String[] propertiesForComparison) {
		this.propertiesForComparison = propertiesForComparison;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		//if (this == obj)
		//	return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Entity otherEntity = (Entity) obj;
		//obter propriedades para compação
		String[] properties = otherEntity.getPropertiesForComparison();
		
		//iterar sobre elas
		int numberIguals=0;
		for (String prop: propertiesForComparison){
			
			if( otherEntity.getPropertiesValuesHaspMap().get(prop)==null
					|| this.getPropertiesValuesHaspMap().get(prop)==null){
				return false;
			}
			
			if( otherEntity.getPropertiesValuesHaspMap().get(prop).equals(
					this.getPropertiesValuesHaspMap().get(prop)) ){
			
				numberIguals++;
			}
		}
		
	
		if (numberIguals== properties.length){
			return true;
		}
	
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 10;
	}
	
}
