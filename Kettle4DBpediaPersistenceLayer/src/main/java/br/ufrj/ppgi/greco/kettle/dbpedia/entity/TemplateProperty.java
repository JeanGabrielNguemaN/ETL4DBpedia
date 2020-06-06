package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class TemplateProperty {
	
	private int id    ;  
	private int   idtemplate    ; 
	private String  templateproperty;
	public TemplateProperty(int idtemplateproperty, int templateId, String templateproperty) {
		this();
		this.id=idtemplateproperty;
		this.idtemplate=templateId;
		this.templateproperty=templateproperty;
	}
	
	public TemplateProperty() {
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdtemplate() {
		return idtemplate;
	}
	public void setIdtemplate(int idtemplate) {
		this.idtemplate = idtemplate;
	}
	public String getTemplateproperty() {
		return templateproperty;
	}
	public void setTemplateproperty(String templateproperty) {
		this.templateproperty = templateproperty;
	}    

}
