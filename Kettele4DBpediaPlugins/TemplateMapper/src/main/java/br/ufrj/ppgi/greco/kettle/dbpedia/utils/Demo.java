package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		;
		
		String[] infos = TemplatesHandler.getTemplates();
		
		
		//for (String info: infos) {
		//	System.out.println(""+info);
		//}
		
		/*
        System.out.println("\n\n\n\n "+TemplatesHandler.getTemplates()[0]);
		
		TemplateMappingManager tmm= new TemplateMappingManager();
		
		TemplateMapping tm = tmm.getTemplateMappingByTemplateByName("Info/Taxonomia");
		
		System.out.println("ID of TM "+tm.getTemplateId());
		HashMap<String, String> propsHashMap = tm.getAtributesMappings();
		Set<String> props = propsHashMap.keySet();
		
		for (String prop:props) {
			System.out.println("PROP ="+prop);
		}
		*/
		String [] array = {"Str 1","Str 3","Str 85","Str 32","Str 89"};
		   Arrays.sort(array);
		   for (int i = 0; i < array.length; i++) {
		   System.out.println(array[i]);
		   };

	}

}
