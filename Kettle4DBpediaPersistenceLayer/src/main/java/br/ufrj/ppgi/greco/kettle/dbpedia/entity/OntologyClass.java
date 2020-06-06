package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class OntologyClass {

	private int id    ;  
	private String  ontologyclass;
	
	public OntologyClass(int idontologyclass, String ontologyclass) {
		this();
		this.id=idontologyclass;
		this.ontologyclass=ontologyclass;
	}
	
	public OntologyClass(){
		
	}

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOntologyclass() {
		return ontologyclass;
	}

	public void setOntologyclass(String ontologyclass) {
		this.ontologyclass = ontologyclass;
	}
}
