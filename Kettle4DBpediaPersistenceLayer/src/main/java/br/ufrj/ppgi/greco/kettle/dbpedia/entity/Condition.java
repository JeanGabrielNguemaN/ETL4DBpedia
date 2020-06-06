package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

public class Condition {

	private int id;
	private int templateid;
	private String templateProperty=null;
	private String operator=null;
	private String propertyValue=null;
	private String mapToClass=null;
	
	public String getTemplateProperty() {
		return templateProperty;
	}

	public void setTemplateProperty(String templateProperty) {
		this.templateProperty = templateProperty;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getMapToClass() {
		return mapToClass;
	}

	public void setMapToClass(String mapToClass) {
		this.mapToClass = mapToClass;
	}

	public Condition(int id, String templateProperty_, String operator_, String propertyValue_, String  mapToClass_) {
		
		this(templateProperty_, operator_, propertyValue_,  mapToClass_);
			
		this.id=id;
	}

	
	public Condition(String templateProperty_, String operator_, String propertyValue_, String  mapToClass_) {
		this.id=id;
		this.templateProperty= templateProperty_;
		this.operator=operator_;
		this.propertyValue=propertyValue_;
		this.mapToClass=mapToClass_;
	}
	public int getTemplateid() {
		return templateid;
	}

	public void setTemplateid(int templateid) {
		this.templateid = templateid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
