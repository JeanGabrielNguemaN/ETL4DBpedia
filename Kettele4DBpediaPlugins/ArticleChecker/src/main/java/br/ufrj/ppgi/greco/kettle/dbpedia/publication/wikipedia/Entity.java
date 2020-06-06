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
