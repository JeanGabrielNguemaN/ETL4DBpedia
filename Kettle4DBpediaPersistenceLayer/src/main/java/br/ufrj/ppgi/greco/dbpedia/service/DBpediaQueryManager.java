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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;

//import org.ufrj.ppgi.greco.dbpedia.DBpediaOntology;
import org.apache.jena.query.Query;

public class DBpediaQueryManager {
	
	private String prefixesStmnts=""+
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n"+
				"PREFIX dbo:<http://dbpedia.org/ontology/>\n"+
				"PREFIX dbr:<http://pt.dbpedia.org/resource/>\n"+
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>"+
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+ 
				"PREFIX owl:<http://www.w3.org/2002/07/owl#> \n"+
				"\n";
	
	/**
	 * Retorna o endereço do endpoint sparql para uma edição.
	 * @param lang
	 * @return
	 */
	private String getSparqlEndpoint(String lang) {
		
		if (lang==null || lang.length()!=2){
	 			return null;
		}
		
	 	return "http://"+ lang.toLowerCase()+".dbpedia.org/sparql/";
	}
	
	
	private final int limit=5000;
	
	

	/**
	 * Obter numero de instâncias por classe da DBpedia
	 * @return
	 * @throws IOException
	 */
	private  Hashtable<String, Integer> getNumberInstancesOfClasses() throws IOException{
		
		Hashtable<String, Integer> classesAndInstances= null;

	    FileReader fr = new FileReader("./files/pt_instancias_por_classeV2");
	    
	    BufferedReader br = new BufferedReader(fr);
	    
	    //primeira linha
	    String header=(String)br.readLine().replace("\"", "");
	    
	    if(header.contains("URI")){
	    	
	    	classesAndInstances = new 	Hashtable<String, Integer> ();
	    	
	    	//System.out.println("PATH = "+path);
	    	while (br.ready()) {
	    		
	    		String linha=br.readLine().replace("\"", "");
	    		
	    		//grantir que a linha nao � vazia
	    		if(!linha.trim().equals("")){
	    	
	    			String[] tokens= linha.split(",");
	    			
	    			classesAndInstances.put(tokens[0], new Integer(tokens[1]));
					
	    		}
	    	}
	    	br.close();
	    }
	   
		return classesAndInstances;
	}
	
	/**
	 * Retorna a frequenica de instancias por classe
	 * @param editionSparqlURL
	 * @param limit
	 */
	//private  HashMap<String,Integer> listClasses(String editionSparqlURL, String[] classes, String[] keyWords, int limit) { 
	private HashMap<String,Integer> listClasses(String editionSparqlURL, String[] keyWords, int limit) { 
				
		if(editionSparqlURL==null || keyWords==null||limit<=0){
			return null;
		}
		
		HashMap<String,Integer> hMap=null;
		
		String queryString=prefixesStmnts+"\n"
	   
				+ " SELECT DISTINCT   ?x ?class ?comment "
				//+" WHERE { ?x a ?class, dbo:Species. "
				+" WHERE { ?x a ?class . "
                +" ?x rdfs:comment ?comment."
				+" FILTER (regex(?x,\"http://pt.dbpedia.org/resource/\",\"i\")"
                +" &&  regex(?class,\"http://dbpedia.org/ontology/\",\"i\")" ;
		        
		// incluir as palavras chaves
		for (int i=0; i<keyWords.length;i++){
			if(i==0){
				queryString+="&&(";
			}
			
			String word=keyWords[i];
			
			queryString+=" regex(?comment,\""+ word+ "\",\"i\") ";
			
			if (i!=keyWords.length-1){
				queryString+=" || ";
				continue;
			}
			//fim
			queryString+=")"
			          +")} LIMIT " +limit+ " ";
		}

		//System.out.println(" QUERY ="+queryString);
		Query query = QueryFactory.create(queryString.toString());

        // Remote execution.
        try ( QueryExecution qexec = 
        		QueryExecutionFactory.sparqlService(editionSparqlURL, query) ) {
            // Set the DBpedia specific timeout.
            //((QueryEngineHTTP)qexec).addParam("timeout", "1000000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            //ou um ou outro
            //ResultSetFormatter.out(System.out, rs, query);
           
            //ou um ou outro
            System.out.println("--------------------------------------------------------------------------");
            
            if(rs==null){
            	return null;
            }
            
            //Para adicionar apenas classes de templates mapeados
            TemplateMappingManager templateMappingManager= 
            		new TemplateMappingManager();
            
            Set<String> mappedClasses=templateMappingManager.listAllMappedClasses();
            
            //Set<CandidateClass> candidateClassesSet= new HashSet<CandidateClass>();
           // Set<String> candidateClassesNameSet= new HashSet<String>();
            hMap= new HashMap<String, Integer>();
           
            /* 
            int k=0;
            for (String c: mappedClasses){
    			System.out.println("MappedClass "+(++k)
    					+ " ="+c+"=");
    		}
            */
            
            while(rs.hasNext()){
            	
            	
            	QuerySolution qs=rs.next();
            	
            	String name= ""+qs.getResource("class");
            	//CandidateClass candidateClass=new CandidateClass(name,1);
            	
            	//candidateClassesNameSet.add(name);
            	
            	//se classe nao estiver mapeadaà ontologia, nao inserir
            	if(!mappedClasses.contains(DBpediaUtils.dboIRItoClassName(name))){
            		//System.out.println("Class "+name+" nao existe");
            		continue;
            	}
            	
            	
            	if(hMap.containsKey(name)){
            		int number=hMap.get(name);
          
            		number++;
            		hMap.remove(name);
            		
            		hMap.put(name, number);
            		continue;
            	}
            	//primeira inclusao
            	
            	hMap.put(name, 1);
            	
            	//System.out.println(++i+" "+qs.getResource("class"));
            	
            }
            
            System.out.println("Count: "+ rs.getRowNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return hMap;
	}
	
	

	
	/**
	 * Retorna as classes candidatas por ordem descrescente
	 * @param classesFreqHashMap
	 * @return
	 */
	//public List<CandidateClass> listClasses(String[] classes,String[] words){
	public List<CandidateClass> listClasses(String[] words){
		
		HashMap<String,Integer> classesFreqHashMap=
				          listClasses(DBpediaUtils.getEndpoint("pt"),words, limit);
		
		if (classesFreqHashMap==null){
			return null;
		}
		
		List<CandidateClass> candidateClassList=new ArrayList<CandidateClass>();
		Set<String> keys=classesFreqHashMap.keySet();
		
		for (String key: keys){
			int number=classesFreqHashMap.get(key);
			
			//para o calcular o percentual
			double number_100= (double)(number*100);
			double percentual= number_100/(double)limit;
			
			//percentual=getDoubleFromFormattedDecimal(null, percentual);
			
			CandidateClass candidateClass = 
					new CandidateClass(DBpediaUtils.dboIRItoClassName(key),
							percentual);
			
			//adicionar
			candidateClassList.add(candidateClass);
		}
		//ordenar por ordem descrescente
		Collections.sort(candidateClassList);
		
		return candidateClassList;
	}

	/**
	 * @param value
	 * @throws NumberFormatException
	 */
	public double getDoubleFromFormattedDecimal(String format, double value) throws NumberFormatException {
		
		if (format==null){
			format= "####0.00";
		}
		
		DecimalFormat  df = new DecimalFormat(format);

		return new Double(df.format(value).replace(",", "."));
	}
}
