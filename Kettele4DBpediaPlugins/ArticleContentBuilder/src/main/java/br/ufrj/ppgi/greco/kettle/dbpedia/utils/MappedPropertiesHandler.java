/*
* %W% %E% Jean Gabriel Nguema Ngomo
*
* Copyright 2021 Jean Gabriel Nguema Ngomo
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class MappedPropertiesHandler {
	
    private static TemplateMappingManager templateMappingManager=new TemplateMappingManager();
	
	public static String[] getAttributes(){
		
		List<String> result = new ArrayList<String>();
		
		String[] infobox_attributes={"nome_oficial","nome_nativo","outro_nome","país","apelido", "lema",
				"tamanho_imagens","fuso_horário_DST","diferença_utc_DST","coord_título","coord_sufixo",
				"latg,latm","lats,latNS","longg","longm","longs",
				"longEW","altitude"};
	

		for (String attribute : infobox_attributes) {
	
			result.add(attribute);
		}
		

		return result.toArray(new String[result.size()]);
	}
	
	
	public static String[] getTemplatesProperties(String templatename){
		
		String templatename_=templatename.split("-")[0].trim();

		TemplateMapping templateMapping = 
							templateMappingManager.getTemplateMappingByTemplateByName(templatename_);
		
		HashMap<String, String> atributesHashMap = templateMapping.getAtributesMappings();
		
		Set<String> props = atributesHashMap.keySet();
		
		return props.toArray(new String[props.size()]);
	}
	
	/**
	 * Obter todos as propriedades mapeadas
	 * @return
	 */
	public static String[] getMappedProperties(){
		
		String[] linhas = readCSV("mappedproperties", ",");
		
		if(linhas==null || linhas.length==0) {
			return null;
		}
		
		//primeira linha contem template: remove-la
		String[] properties=new String[linhas.length-1];
		
		for(int i=1; i<linhas.length;i++) {
			
			String property= linhas[i];
			if(property.equals("")) {
				continue;
			}
			int j=i-1;
			properties[j]=property;
			
		}
	
		return properties;
	}
	
	
	/**
	 * Ler linhas de um arquivo CSV
	 * @param filename
	 * @param separator
	 * @return
	 */
	private static String[] readCSV(String filename, String separator){
		
		ArrayList<String> linhas= new ArrayList<String>();
		
		try {    

			BufferedReader br = new BufferedReader(
					  new InputStreamReader(new FileInputStream(repositorydir+filename), "UTF-8"));
		       
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
