package br.ufrj.ppgi.greco.kettle.dbpedia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.dbpedia.service.CandidateClass;
import br.ufrj.ppgi.greco.dbpedia.service.DBpediaQueryManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

/*
 * Recupera templates que são apresentados ao usuário.
 */
public class TemplateSelector {
	private TemplateMappingManager tmManager;
	private DBpediaQueryManager dbpediaQueryManager;
	
	public TemplateSelector(){
		tmManager= new TemplateMappingManager();
		dbpediaQueryManager= new DBpediaQueryManager();
	}
	
	
	/**
	 * Retorna os templates associados à palavras chaves digitadas.
	 * Estas palavras estas presentes em "rdfs:comment" dos recursos. 
	 * As classes destes recursos são selecionadas.E depois, são selecionados templates que mapeaim para estas classes. 	 
	 * Este metodo esconde detalhes de implementação.
	 * @param words
	 * @return
	 */
	public Set<Template> selectTemplates(String[] words){
		
		Set<Template> templates=null;
		
		//buscar as classes candidatas com base nas palavras chaves
		List<CandidateClass> lista = dbpediaQueryManager.listClasses(words);
		
		if (lista==null || lista.size()==0){
			return null;
		}
		
		templates= new HashSet<Template>();
		
		for (CandidateClass c: lista){
			//System.out.println("Nome :"+c.getClassname()+"= - number: "+c.getNumberOfInstance());
			
			Set<Template> classTemplates  = 
					tmManager.listTemplatesMappedToClasses(c.getClassname());
			
			if(classTemplates!=null){
				templates.addAll(classTemplates);
			}
		}
		
		return templates;
		
	}
	
	/**
	 * Retorna os templates associados à palavras chaves digitadas.
	 * Estas palavras estas presentes em "rdfs:comment" dos recursos. 
	 * As classes destes recursos são selecionadas.E depois, são selecionados templates que mapeaim para estas classes. 	 
	 * Este metodo esconde detalhes de implementação.
	 * @param words
	 * @return
	 */
	public List<TemplateCandidate> selectTemplateCandidates(String[] words){
		
		Set<TemplateCandidate> templates=null;
		
		//buscar as classes candidatas com base nas palavras chaves
		List<CandidateClass> lista = dbpediaQueryManager.listClasses(words);
		
		if (lista==null || lista.size()==0){
			return null;
		}
		
		templates= new HashSet<TemplateCandidate>();
		
		for (CandidateClass c: lista){
			//System.out.println("Nome :"+c.getClassname()+"= - number: "+c.getNumberOfInstance());
			
			Set<Template> classTemplates  = 
					tmManager.listTemplatesMappedToClasses(c.getClassname());
			
			if(classTemplates!=null){
				for(Template template: classTemplates){
					
					
					if( !isValidTemplate(template)){	
						continue;
					}
					
					TemplateCandidate templateCandidate= 
							new TemplateCandidate(template, c.getNumberOfInstance());
					
					templates.add(templateCandidate);
				}
				
			}
		}
		
		
		//TemplateCandidate[] arr = (TemplateCandidate[])templates.toArray(new TemplateCandidate[0]);
		TemplateCandidate[] arr = (TemplateCandidate[])templates.toArray(new TemplateCandidate[0]);
		
		ArrayList<TemplateCandidate> templatesArr= new ArrayList<TemplateCandidate>(Arrays.asList(arr));
		
		//ordenar
		Collections.sort(templatesArr);
		
		//retornar apenas 20
		if (templatesArr.size()<=20){
			return templatesArr;
		}
		//retornar apenas 20 elementos
		return templatesArr.subList(0, 20);
		
	}


	/**
	 * @param template
	 * @return
	 */
	public boolean isValidTemplate(Template template) {
		
		if(template==null){
			return false;
		}
		
		if(template.getTitle()==null){
			return false;
		}
		
		if (template.getTitle().contains("Província de")){
			return false;
		}
		if (template.getTitle().contains("Província da")){
			return false;
		}
		
		if (template.getTitle().contains("Cidade da")){
			return false;
		}
		
		
		return true ;
	}
}
