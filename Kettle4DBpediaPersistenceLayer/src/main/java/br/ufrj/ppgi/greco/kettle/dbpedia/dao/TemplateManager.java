package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class TemplateManager {
   private TemplateDAO templateDAO;
   
   public TemplateManager() {
	// TODO Auto-generated constructor stub
	   templateDAO= new TemplateDAO();
	  
   }
   
  public List<Template> listTemplates(){
	   
	   return  templateDAO.listTemplates();
	   
   }
  
  public void instert(String templateName){
	  templateDAO.insert(templateName);
  }
  
  
  public void instertOrUpdate(String templateName){
	  templateDAO.instertOrUpdate(templateName);
  }
  
  public int instert(Template template){
	 return templateDAO.insert(template);
  }
  
  
  public Template getTemplate(String templateName){
	  
	   return  templateDAO.getTemplate(templateName);  
 }

}
