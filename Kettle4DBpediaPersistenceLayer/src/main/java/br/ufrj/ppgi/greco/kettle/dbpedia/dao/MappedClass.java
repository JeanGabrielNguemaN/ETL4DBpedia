package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

public class MappedClass {
	private int idtemplate;
	private int id ;
	private String ontologyclass;
	
	public MappedClass(int id2, int idtemplate2, String ontologyclass2) {
		// TODO Auto-generated constructor stub
		this.idtemplate=idtemplate2;
		this.id=id2 ;
		this.ontologyclass=ontologyclass2;
	}
	
	public int getIdtemplate() {
		return idtemplate;
	}
	public void setIdtemplate(int idtemplate) {
		this.idtemplate = idtemplate;
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
