package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Obter o mapeamentos de infobox da ontlogia DBpedia
 */
public class JSoupInfoboxDemo {
	
	static String PREFIXO_URL="http://mappings.dbpedia.org/index.php/Mapping_pt:Info/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		extractFromURL("Pessoa");
		
		//extractFromFile("./infobox/Mapping_Infobox_Cidade.html");
		
	}

	/**
	 * Le template do infobox do arquivo, e retorna os mapeamentos de infobox para ontologia DBpedia.
	 * @param filename arquivo
	 */
	public static void extractFromFile(String filename) {
		File input = new File("./infobox/Mapping_Infobox_Cidade.html");
		try {
			Document doc = Jsoup.parse(input, "UTF-8", "http://infobox.com/");
			
			//Seleciona Body
			Element body = doc.select("body").first();
			
			//System.out.println(body);
			
            //Element globalWrapper=body.select("#bodyContent").first();
			
			 Elements wikitableElements=body.select(".wikitable");
			 
			 //Tabela da Classe do infobox
			 Element wikitableClassType=wikitableElements.first();
			 
			//System.out.println(wikitableClassType);
			System.out.println("-----------------Quantidade "+wikitableElements.size()+"-------------------------\n\n");
			 
			Element className=wikitableClassType.select("a[href]").get(1);
			 
		
			System.out.println("Cidade --> " + className.text());
			
			int size=wikitableElements.size();
			
			//System.out.println("Template Property\tOntology Property");
			
			System.out.println("Template_Property;Ontology_Property");
			for (int i=1; i<size; i++){
				Element propertyTableElement=wikitableElements.get(i);
				Elements td_Elements=propertyTableElement.select("td");
				String template_property=td_Elements.get(1).text();
				
				Elements td_ont_prop=td_Elements.get(3).select("a[href]");
				
				String ontology_property="";
				ontology_property=td_ont_prop.text();
				if(!ontology_property.equals("")){
					
					//System.out.println(i+"-"+template_property+ "\t"+ontology_property );
					System.out.println(template_property+ ";"+ontology_property );
					
				}
				//
			
				
			}
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Informa conceito, ex. cidade, e retorna os mapeamentos de infobox para ontologia DBpedia.
	 * @param concept
	 */
	public static void extractFromURL(String concept) {
		
		try {
			Document doc = Jsoup.connect(PREFIXO_URL+concept).get();
			
			//Seleciona Body
			Element body = doc.select("body").first();
			
			//System.out.println(body);
			
            //Element globalWrapper=body.select("#bodyContent").first();
			
			 Elements wikitableElements=body.select(".wikitable");
			 
			 //Tabela da Classe do infobox
			 Element wikitableClassType=wikitableElements.first();
			 
			//System.out.println(wikitableClassType);
			System.out.println("-----------------Quantidade "+wikitableElements.size()+"-------------------------\n\n");
			 
			Element className=wikitableClassType.select("a[href]").get(1);
			 
		
			System.out.println("Cidade --> " + className.text());
			
			int size=wikitableElements.size();
			
			//System.out.println("Template Property\tOntology Property");
			
			System.out.println("Template_Property;Ontology_Property");
			for (int i=1; i<size; i++){
				Element propertyTableElement=wikitableElements.get(i);
				Elements td_Elements=propertyTableElement.select("td");
				String template_property=td_Elements.get(1).text();
				
				Elements td_ont_prop=td_Elements.get(3).select("a[href]");
				
				String ontology_property="";
				ontology_property=td_ont_prop.text();
				if(!ontology_property.equals("")){
					
					//System.out.println(i+"-"+template_property+ "\t"+ontology_property );
					System.out.println(template_property+ ";"+ontology_property );
					
				}	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
