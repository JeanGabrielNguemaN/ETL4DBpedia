package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class PropertyMapping {

	
	private String templateProperty;
	private String ontologyProperty;
	private String unit;
	public PropertyMapping( String templateProperty, 
			String ontologyProperty,  String unit){
		
		this.templateProperty=templateProperty;
		this.ontologyProperty=ontologyProperty;
		this.unit=unit;
	}
	public String getTemplateProperty() {
		return templateProperty;
	}
	public void setTemplateProperty(String templateProperty) {
		this.templateProperty = templateProperty;
	}
	public String getOntologyProperty() {
		return ontologyProperty;
	}
	public void setOntologyProperty(String ontologyProperty) {
		this.ontologyProperty = ontologyProperty;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
}
