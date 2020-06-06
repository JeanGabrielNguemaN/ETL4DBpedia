package br.ufrj.ppgi.greco.kettle.dbpedia.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class TemplateMapping {
	
	private String templateName;
	private int templateId;
	private ArrayList<String> mappedClasses;
	private HashMap<String,String> atributesMappings;
	private ArrayList<Condition> conditions;
	private boolean conditionalMapping=false;
	private String mappings;
	
	//constructor
		public TemplateMapping(String templateName, String mappings) {
			//
			this.templateName=templateName;
			this.mappings=mappings;
		}
		
		public TemplateMapping(int templateId, String mappings) {
			//
			this.templateId=templateId;
			this.mappings=mappings;
		}
		
		public TemplateMapping(int templateId) {
			//
			this.templateId=templateId;
			this.mappings=mappings;
		}
	
	public String getMappings() {
		return mappings;
	}
	public void setMappings(String mappings) {
		this.mappings = mappings;
	}
	
	public ArrayList<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(ArrayList<Condition> conditions) {
		this.conditions = conditions;
	}
	public boolean isConditionalMapping() {
		return conditionalMapping;
	}
	public void setConditionalMapping(boolean conditionalMapping) {
		this.conditionalMapping = conditionalMapping;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public ArrayList<String> getMappedClasses() {
		return mappedClasses;
	}
	public void setMappedClasses(ArrayList<String> mappedClasses) {
		this.mappedClasses = mappedClasses;
	}
	public HashMap<String, String> getAtributesMappings() {
		return atributesMappings;
	}
	public void setAtributesMappings(HashMap<String, String> atributesMappings) {
		this.atributesMappings = atributesMappings;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	
	
	
	

}
