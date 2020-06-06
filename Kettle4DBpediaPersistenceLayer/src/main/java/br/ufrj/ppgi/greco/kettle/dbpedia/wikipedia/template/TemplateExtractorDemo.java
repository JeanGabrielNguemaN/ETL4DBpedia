package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import java.util.ArrayList;

public class TemplateExtractorDemo {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TemplateExtractor extractor= new TemplateExtractor();
		String pattern="Info/Taxonomia";
		
		//System.out.println(demo.searchByName(srsearch, srnamespace, srlimit, sroffset));
		
		//Obter todos os templates que começam "/Info", sem exceção
		ArrayList<Template> templates= extractor.getAllTemplates(pattern);
		
		
		
		//Dentro dos templates, recuerar apenas apenas que sao de Infobox
		templates= extractor.getInfoboxTemplates(templates);
		
		System.out.println("Infobox template Size = "+templates.size());
		
		System.out.println("All template Size = "+templates.size());
		
		printArrayList(templates);
		
		//processCategories(demo, templates);
		
		/*
		String str="Predefinição:Info/Produto botânico/doc";
		String[] tokens= str.split("Info/");
		System.out.println("tokens = "+tokens[0]);
		*/
		
	}

	
	public static void printArrayList(ArrayList<Template> templates) {
		
		for(Template template: templates){
			System.out.println(template.getTitle());
		}
	}
	
}
