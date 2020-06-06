package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class TemplatesHandler {
	
    private static TemplateMappingManager templateMappingManager=new TemplateMappingManager();
	
    /**
     * Obter as propriedades do templates
     * @param templatename
     * @return
     */
	public static String[] getTemplatesProperties(String templatename){
		
		if(templatename==null || templatename.trim().equals("")) {
			
			String[] arr = new String[]{""};
			return arr;
		}
		String templatename_=templatename.split("-")[0].trim();

		
		TemplateMapping templateMapping = 
							templateMappingManager.getTemplateMappingByTemplateByName(templatename_);
		
		if(templateMapping==null) {
			return  new String[]{""};
		}
		
		HashMap<String, String> atributesHashMap = templateMapping.getAtributesMappings();
		
		Set<String> props = atributesHashMap.keySet();
		
		return props.toArray(new String[props.size()]);
	}
	
	/**
	 * Obter todos os templates. 
	 * @return String[]. Formato do item: Templatename - freq %
	 */
	public static String[] getTemplates(){
		
		String[] templateEntries = retrieve("templates");
		
		if(templateEntries==null || templateEntries.length==0) {
			return null;
		}
		
		String[] templates=new String[templateEntries.length];
		
		int i=0;
		for(String linha: templateEntries) {
			if(linha.equals("")) {
				continue;
			}
			String[] tokens=linha.split(",");
			
			String name=tokens[1].trim();
			String frequence=tokens[3].trim();
			
			templates[i++]=name +" - "+ frequence+"";
		}
	
		return templates;
	}
	
	
	/**
	 * Obter string[] de um repositorio
	 * @param repository
	 * @param separator
	 * @return
	 */
	private static String[] retrieve(String repository){
		
		ArrayList<String> linhas= new ArrayList<String>();
		
		try {    

			BufferedReader br = new BufferedReader(
					  new InputStreamReader(new FileInputStream(repositorydir+repository), "UTF-8"));
		       
			while (br.ready()) {
				
				linhas.add(br.readLine().trim());
		    }

	         //fechar o arquivo
			 br.close();
			 
			} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();														
			} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();														
			}
		
		return linhas.toArray(new String[linhas.size()]); 		
	}
	
	 private static String repositorydir= "C:\\DBpedia\\Kettle\\pdi-ce-8.2.0.0-342\\data-integration\\plugins\\steps\\etl4lodrepositories\\";
		
}
