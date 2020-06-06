package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.parser;

public class ValueAndDMLMappings {
	
	private String value;
	
	private String restOfDMLMappings;

	public ValueAndDMLMappings(String stringValue, String stringRestOfMapping) {
		
		this.value=stringValue;
		
		this.restOfDMLMappings=stringRestOfMapping;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRestOfDMLMappings() {
		return restOfDMLMappings;
	}

	public void setRestOfDMLMappings(String restOfDMLMappings) {
		this.restOfDMLMappings = restOfDMLMappings;
	}
	

}
