package br.ufrj.ppgi.greco.dbpedia.ontology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

public class DBpediaOntology {
	
	public static final String ONTOLOGIES_DIR = "./resources/ontologies/";
	public static final String BASE_URL = "http://jgnn.ufrj.ppgi.com/dataset/politicos/schema/";
	public static final String RES_URL = "http://jgnn.ufrj.ppgi.com/dataset/politicos/resource/";
	static final String owlURL = "http://www.w3.org/2002/07/owl#";
	static final String dboURL = "http://dbpedia.org/ontology/";
	
	static OntModel ontologyModel=null;
	
	//public static final String BASE_URL = "http://data.kasabi.com/dataset/cheese/schema/";
	
	
	public static OntModel getOntologyModel() {
		//OWL_DL_MEM_RDFS_INF
		ontologyModel= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		
		InputStream in=null;
		
		try {
			in = new FileInputStream(ONTOLOGIES_DIR+"dbpedia_2016-10.owl");
			
			ontologyModel.read(in, "http://dbpedia.org/ontology/");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ontologyModel;
	
	}
	
	static OntClass getClass(String uri){
		getOntologyModel();
		//return ontologyModel.getOntClass(dboURL+className);
		return ontologyModel.getOntClass(uri);
	}
	
	static void printModel(OntModel ontoModel){
		ontoModel.write(System.out);
	}
	
	static void listTriples(OntModel ontoModel){
		
		StmtIterator stmtI=ontoModel.listStatements();
		
		while( stmtI.hasNext()){
			Statement statement=stmtI.next();
			System.out.println(statement.getSubject());
			System.out.println(statement.getPredicate());
			System.out.println(statement.getObject());
			System.out.println("------------------------------------");
		}
	}
	
	static void listPropertiesOf(OntClass ontClass){
		
		StmtIterator stmtI=ontClass.listProperties();
		
		System.out.println("Classe : "+ontClass.getURI());
		
		while( stmtI.hasNext()){
			Statement statement=stmtI.next();
			System.out.println(statement);
			System.out.println("---");
		}
	}
	
	static void listSubclasses(OntClass ontClass){
		
		ExtendedIterator<OntClass> extendedIter=ontClass.listSubClasses(true);
		
		System.out.println("Listing subClasses of : "+ontClass.getURI());
		
		int i=0;
		
		while( extendedIter.hasNext()){
			i++;
			OntClass ontClassIt=extendedIter.next();
			
		
			//if(deputy!=null && ontClassIt.hasSubClass(deputy, true))
		  System.out.println(ontClassIt);
			
		}
		System.out.println("i= "+i +" ....END....");
	}
	
static void listSuperclasses(OntClass ontClass){
		
		ExtendedIterator<OntClass> extendedIter=ontClass.listSuperClasses(false);
		
		System.out.println("Listing superClasses of : "+ontClass.getURI());
		
		while( extendedIter.hasNext()){
			OntClass ontClassIt=extendedIter.next();
			System.out.println(ontClassIt);
			
		}
	}
	
 static void saveModel(OntModel ontModel, String filename){
		
	 
	 OutputStream out=null;
		try {
			out = new FileOutputStream(ONTOLOGIES_DIR+filename);
			
			ontModel.write(out,"RDF/XML");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
 
 static void addDeputyIndModel(){
	 
	 String nome= "JOSEILDO RIBEIRO RAMOS";
	 String uf="BA";
	 OntClass deputadoClass= ontologyModel.getOntClass( BASE_URL+"Deputado");
	 
	 
	
	 Individual deputadoIns= ontologyModel.createIndividual( RES_URL+"JOSEILDO_RIBEIRO_RAMOS", deputadoClass);
	 Property nomeProp=ontologyModel.getProperty(BASE_URL+"nome");
	 
	
	 deputadoIns.setPropertyValue(nomeProp, ontologyModel.createLiteral(nome));
	 
	 deputadoIns.addProperty(ontologyModel.getOntProperty(BASE_URL+"uf"),uf);
	 
	}
	

 
static void addDeputyIndModelV2(){
	 
	String nome= "JOSEILDO RIBEIRO RAMOS";
	 String uf="BA";
	 OntClass deputadoClass= ontologyModel.getOntClass( BASE_URL+"Deputado");
	 
	 
	 Model model= ModelFactory.createDefaultModel();
	 
	 model.setNsPrefix("plt", RES_URL);
	 model.setNsPrefix("plto", BASE_URL);
	 
	 //System.out.println("deputadoClass="+ deputadoClass);
	 
	 Individual deputadoIns= deputadoClass.createIndividual( RES_URL+"JOSEILDO_RIBEIRO_RAMOS" );
	 //model.createResource(deputadoIns.asResource());
	
	 //listResouceTriples(deputadoIns.asResource());
	 
	 StmtIterator stmtI=deputadoIns.asResource().listProperties();
	 //adicionar a lista de statement no modelo
	 model. add(stmtI.toList());
	 
	 model.add(deputadoIns.asResource(),  RDF.type, deputadoClass);
	 
	 Property nomeProp=ontologyModel.getProperty(BASE_URL+"nome");
	 model.add(deputadoIns.asResource(),nomeProp,model.createLiteral(nome, "PT"));
	 
	 Property ufProp=ontologyModel.getOntProperty(BASE_URL+"uf");
	 model.add(deputadoIns.asResource(),  ufProp,model.createLiteral(uf, "PT"));
	 
	 model.write(System.out);
	 
	}


static void listResouceTriples(Resource resource){
	
	StmtIterator stmtI=resource.listProperties();
	
	System.out.println("Propriedades do Recurso: "+ resource.getURI());
	
	while( stmtI.hasNext()){
		Statement statement=stmtI.next();
		System.out.print(statement.getSubject()+" **"+statement.getPredicate()+" **"+statement.getObject()+"\n");
		System.out.println("------------------------------------");
	}
}


public static void mainDpedia() {
	// TODO Auto-generated method stub

	//no entailments
	//OntModel ontoModel= ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	//entailments e inferences; Memory overhead
	//OntModel ontoModel= ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
	//entailments e inference mais completos:memory overhead
	ontologyModel= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
	
	InputStream in=null;
	
	try {
		in = new FileInputStream(ONTOLOGIES_DIR+"dbpedia_2016-10.owl");
		
		ontologyModel.read(in, "http://dbpedia.org/ontology/");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	System.out.println("SIZE == "+ ontologyModel.size());
	
	//printModel(ontoModel);
	
	//listTriples(ontoModel);
	
	OntClass deputado= ontologyModel.getOntClass(BASE_URL+"Deputado");
	deputado.addLabel("Deput�","FR");
	
	listPropertiesOf(deputado);
	//criar uma nova classe na ontologia
	OntClass vereador= ontologyModel.createClass(BASE_URL+"Vereador");
	
	vereador.addSuperClass(ontologyModel.getOntClass(BASE_URL+"Politico"));
	vereador.addLabel("Vereador", "PT");
	
	
	
	OntProperty nomeProp=ontologyModel.getOntProperty(BASE_URL+"nome");
	nomeProp.addLabel("Nome completo", "PT");
	
	ontologyModel.getOntProperty(BASE_URL+"uf").addLabel("Unidade Federativa: Estado", "PT");
	
    System.out.println(""+nomeProp);
	
	addDeputyIndModelV2();
    //addDeputyIndModel();
	
	//printModel(ontoModel);
	
	
	//listSuperclasses(ontoModel.getOntClass(BASE_URL+"DeputadoFederal"));
	//listSubclasses(ontoModel.getOntClass(BASE_URL+"Politico"));
	//saveModel(ontoModel, "PoliSAVED.xml");
	

}

  static void printSuperClasses(OntClass ontClass){
	  //requere ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
	  //OntClass deputy= ontoModel.getOntClass(dboURL+"Deputy");
	  //System.out.println(" SUPER... : "+deputy.getSuperClass());
		
		ExtendedIterator<OntClass> superclasses=ontClass.listSuperClasses(false);
		
		while( superclasses.hasNext()){
			 OntClass superclass=superclasses.next();
			 System.out.println( superclass);
		}
		
  }
  /**
   * Obter o alias da uri com prefixo
   * @param uri
   * @return
   */
  static String uriToDBO(String uri){
	  String dbo=null;
	  
	  if( uri!=null){
		  if(uri.contains("dbo:")){
			  dbo=uri;
		  }else if (uri.contains("http:")){
			dbo="dbo:"+(uri.split("[/]")[4]).trim();  
		  }
	  }
	  return dbo;
  }
	

}
