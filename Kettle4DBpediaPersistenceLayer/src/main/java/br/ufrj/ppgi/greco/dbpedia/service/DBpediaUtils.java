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
