package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class Utilities {

	
	/**
	 * Extrair os mapeamentos de um template de infobox no site da DBpedia Wiki Mapping.
	 * @param infoboxTemplate. Formato: Info/Pessoa
	 * return mappings em dml
	 */
	public static String extractDMLMappings(String infoboxTemplate) {
	
		//Setup return
		String mappings=null;
		//URL padrão que contem template com DML - linguagem de mapeamentos
		String url="http://mappings.dbpedia.org/index.php?title=Mapping_pt:"+
					infoboxTemplate+"&action=edit";

		try {
			//scrapping da página
			Document doc = Jsoup.connect(url).get();
			
			//Seleciona Body
			Element body = doc.select("body").first();
			
			//System.out.println("BODY ="+body.text());
			
			 //Seleciona o elemento TextArea que contem os mapeamentos
			 Element textArea=body.selectFirst("#wpTextbox1");
			 
			 //Se o elemento nao exisitir
			 if(textArea==null){
				 return null;
			 }
				 //obter o conteúdo dos mapeamentos
				 mappings=textArea.text();
				 //System.out.println(mappings);
				 
		
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mappings;
	}
}
