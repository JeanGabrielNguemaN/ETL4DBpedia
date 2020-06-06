package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class OntologyProperty {

	
	private int id    ;  
	

	private String  ontologyproperty;
	
	public OntologyProperty(int idontologyproperty, String ontologyproperty) {
		this();
		this.id=idontologyproperty;
		this.ontologyproperty=ontologyproperty;
	}
	
	public OntologyProperty(){
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOntologyproperty() {
		return ontologyproperty;
	}

	public void setOntologyproperty(String ontologyproperty) {
		this.ontologyproperty = ontologyproperty;
	}
}
