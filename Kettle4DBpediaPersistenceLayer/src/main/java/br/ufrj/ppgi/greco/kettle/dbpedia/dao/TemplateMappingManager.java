package br.ufrj.ppgi.greco.kettle.dbpedia.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class TemplateMappingManager {
   
   private TemplateMappingDAO templateMappingDAO;
   
   public TemplateMappingManager() {
	// TODO Auto-generated constructor stub
	   templateMappingDAO= new TemplateMappingDAO();
   }
    /**
     * Obter o template por id
     * @param id
     * @return
     */
	public TemplateMapping getTemplateMappingByTemplateId(int id) {
		// TODO Auto-generated method stub
		return templateMappingDAO.getTemplateMappingByTemplateId(id);
	}
	
	
	   /**
     * Obter o template por nome
     * @param id
     * @return
     */
	public TemplateMapping getTemplateMappingByTemplateByName(String templatename) {
		
		return templateMappingDAO.getTemplateMappingByTemplateByName(templatename);
	}
	/**
	 * Obter classes para as quais um template mapeia
	 * @param idtemplate
	 * @return
	 */
	public Set<String> listMappedClassesByTemplateId(int idtemplate) {
		// TODO Auto-generated method stub
		return templateMappingDAO.listMappedClassesByTemplateId(idtemplate);
	}
	
	/**
	 * Obter classes para as quais os templates são mapeiados
	 * @return
	 */
	public Set<String> listAllMappedClasses() {
		// TODO Auto-generated method stub
		Set<String> classes=new HashSet<String>();
		
		List<TemplateMapping> templatesMap = templateMappingDAO.getTemplateMapppings();
		
		for(TemplateMapping tm:templatesMap ){
			Set<String> classes_=
					templateMappingDAO.listMappedClassesByTemplateId(tm.getTemplateId());
			
			if (classes_==null){
				continue;
			}
			classes.addAll(classes_);
			
		}
		return classes;
	}
	/**
	 * Obter templates que mapeiam para uma classe pelo nome da classe
	 * @param classeName
	 * @return
	 */
	public Set<Template> listTemplatesMappedToClasses(String classeName) {
		// TODO Auto-generated method stub
		return templateMappingDAO.listTemplatesMappedToClasses(classeName);
	}
	
	/**
	 * Inserir um templatemapping
	 * @param templateMapping
	 */
	public void insertTemplateMapping(TemplateMapping templateMapping){
		 
		 templateMappingDAO.insertTemplateMapping(templateMapping);
	 }
}
