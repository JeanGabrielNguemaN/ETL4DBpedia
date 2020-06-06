package br.ufrj.ppgi.greco.dbpedia.service;

public class DBpediaUtils {

	public static String getEndpoint(String lang) {
	    //DBpedia PT
		if (lang==null){
			return null;
		}
		if (lang.toLowerCase().equals("pt")){
			return "http://pt.dbpedia.org/sparql/";
		}	
		//DBpedia EN	
		if (lang.toLowerCase().equals("en")){
			return "http://dbpedia.org/sparql/";
		}
		
		return null;
	}
	
	/**
	 * Converter http://dbpedia.org/ontology/Concept para Concpet.
	 * @param dboFullIRI
	 * @return
	 */
	public static String dboIRItoClassName(String dboFullIRI) {
	    //DBpedia PT
		if (dboFullIRI==null){
			return null;
		}
		
		if (!dboFullIRI.contains("http://dbpedia.org/ontology/")){
			return null;
		}
		
		return dboFullIRI.split("/")[4];
	
	}
}
