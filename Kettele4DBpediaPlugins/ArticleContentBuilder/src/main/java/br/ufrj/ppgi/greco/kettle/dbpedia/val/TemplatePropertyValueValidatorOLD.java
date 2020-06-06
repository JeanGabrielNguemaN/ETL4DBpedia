package br.ufrj.ppgi.greco.kettle.dbpedia.val;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplatePropertyDAO;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class TemplatePropertyValueValidatorOLD {
	
	private static String prefixesStmnts=""+
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
			"PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n"+
			"PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n"+
			"PREFIX dbo:<http://dbpedia.org/ontology/>\n"+
			"PREFIX dbr:<http://dbpedia.org/resource/>\n"+
			"PREFIX dc: <http://purl.org/dc/elements/1.1/>"+
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+ 
			"PREFIX dbrpt:<http://pt.dbpedia.org/resource/>\n"+
			"PREFIX owl:<http://www.w3.org/2002/07/owl#> \n"+
			"\n";


	public static void main(String[] args) {
		String value="Plantae";// = [[]]
		String templateProperty="reino";
		 int idtemplate=1;
		 
		ValidationResult validationResult = 
				validate(idtemplate, templateProperty , value);
		
		System.out.println("isValid? "+validationResult.isValid());
		System.out.println("Value : "+validationResult.getValue());
		
	    	
	}

	/**
	 * Validar o valor da propriedade de um template considerando
	 * a o tipo (owl:ObjectProperty ou owl:DataProperty) 
	 * da propriedade da Ontology para a  qual a propriedade de template 
	 * é associado. No caso de dataype, é verificado 
	 * se a forma lexical do literal do tipo correspondente (int, double,
	 * float, etc) é correta.
	 * @param idtemplate
	 * @param templateProperty
	 * @param value
	 * @return
	 */
	private static ValidationResult validate(int idtemplate, String templateProperty , String value) {
		
		boolean isValid=true;
		
	    TemplateMappingManager tmm= new TemplateMappingManager();
	    
		TemplateMapping templateMapping = tmm.getTemplateMappingByTemplateId(idtemplate);
		
		HashMap<String, String> propsClassesHashMap = templateMapping.getAtributesMappings();
		//String ontologyproperty="dbo:"+propsClassesHashMap.get(templateProperty);

		String ontologyproperty="dbo:birthDate";
		
		String[] propertytypeArr=getPropertyType(ontologyproperty);
		System.out.println("class  ="+ontologyproperty);
		
		System.out.println("Value (antes)="+value);
		
		//É propriedade do tipo ObjectProperty?
		if(isObjectProperty(propertytypeArr)) {
			System.out.println("isObjectProperty? ="+isObjectProperty(propertytypeArr));
			if (!isResource(value)) {
				value="[["+value+"]]";
			}
			System.out.println("Value (depois)="+value);
			return new ValidationResult(true, value);
		}
		//É propriedade do tipo DatatypeProperty?
		String xsdDatatype = getPropertyDataType(ontologyproperty);
		
		System.out.println("xsdDatatype="+xsdDatatype);
		
		if(xsdDatatype.contains("http://www.w3.org/2001/XMLSchema#")) {
			String typename=xsdDatatype.split("#")[1];
			//String typename = "gYear";
			
			//System.out.println("typename ="+typename);
			
			//Esta classe da API Jena possibilita validar 
			//Verificar se um valor literail para um tipo especifico
			// é correto
			XSDDatatype xSDDatatype=new XSDDatatype(typename);
			
		    String lexicalForm=value;
		    
			isValid = xSDDatatype.isValid(lexicalForm);
			
			//System.out.println("isValid = "+isValid);
			
			//System.out.println("Value (depois)="+value);
		}
		
		return new ValidationResult(isValid, value);
		
	}

	/**
	 * Verifica se o valor corresponde a um recurso
	 * @param value
	 * @return
	 */
	private static boolean isResource(String value) {
		if(value==null || value.length()<=4) {
			return false;
		}
		
		if(value.trim().substring(0,2).equals("[[")) {
			return true;
		}
		
		return false;
	}

	private static boolean isObjectProperty(String[] propertytypeArr) {
		
		for(String prop: propertytypeArr) {
			if(prop.contentEquals("http://www.w3.org/2002/07/owl#ObjectProperty")) {
				
				return true;
				
			}
		}
		return false;
	}
	
	private static boolean isDataProperty(String[] propertytypeArr) {
		
		for(String prop: propertytypeArr) {
			if(prop.contentEquals("http://www.w3.org/2002/07/owl#DatatypeProperty")) {
				
				return true;
				
			}
		}
		return false;
	}

	/**
	 * Obter os tipos da propriedade
	 * @param ontologyproperty
	 * @return
	 */
	private static String[] getPropertyType(String ontologyproperty) {
		
		String[] propertytypeArr=null;
		StringBuffer buffer= new StringBuffer();
		
		String queryString=prefixesStmnts+
				" SELECT  DISTINCT ?type WHERE  {"
				+ ontologyproperty+"  a ?type} LIMIT 10";
		
		org.apache.jena.query.Query   query  = QueryFactory.create(queryString.toString());

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql/", query) ) {
            // Set the DBpedia specific timeout.
            //((QueryEngineHTTP)qexec).addParam("timeout", "1000000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            
            
            int n=0, k=0;
            HashSet<Integer> codes= new HashSet<Integer>();
            
            if(rs==null){
            	return propertytypeArr;
            }
          
            while(rs.hasNext()){
            	
            	QuerySolution qs=rs.next();
            	
            	String type = qs.getResource("type").getURI();
            	
            	buffer.append(""+type+";");
            
            	//System.out.println(type);
            	
            }
          
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return buffer.toString().split(";");
	}
	
	
	
private static String getPropertyDataType(String ontologyproperty) {
		
		String[] propertytypeArr=null;
		StringBuffer buffer= new StringBuffer();
		
		String queryString=prefixesStmnts+
				" SELECT  DISTINCT ?type WHERE  {"
				+ ontologyproperty+" rdfs:range ?type . "
				+ "FILTER REGEX(?type, \"http://www.w3.org/2001/XMLSchema#\",\"i\")} LIMIT 20";
		
		org.apache.jena.query.Query   query  = QueryFactory.create(queryString.toString());

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql/", query) ) {
            // Set the DBpedia specific timeout.
            //((QueryEngineHTTP)qexec).addParam("timeout", "1000000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            
            int n=0, k=0;
            HashSet<Integer> codes= new HashSet<Integer>();
            
            if(rs==null){
            	return null;
            }
          
            while(rs.hasNext()){
            	
            	QuerySolution qs=rs.next();
            	
            	String type = qs.getResource("type").getURI();
            	
            	buffer.append(""+type+";");
            
            	System.out.println(type);
            	
            }
          
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		return buffer.toString().split(";")[0];
		
	}
	
}
