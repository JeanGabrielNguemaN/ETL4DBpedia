package br.ufrj.ppgi.greco.dbpedia.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.ufrj.ppgi.greco.kettle.dbpedia.templates.TemplatesSearcher;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;
import org.ufrj.ppgi.greco.kettle.dbpedia.templates.TemplateCandidate;


public class DBpediaQueryManagerTest {

	static public void main(String...argv) throws IOException
    {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		System.out.println("Start ...: "+dtf.format(LocalDateTime.now()) );
		
		//test_isSubclassOfClass("Person","Deputy");
		DBpediaQueryManager queryManager = new DBpediaQueryManager();
		
		
		String[] words= new String[]{"Planta","planta"};
		//String[] classes= new String[]{"Species","Person"};
		//List<CandidateClass> lista = queryManager.listClasses(words);
		
		//for (CandidateClass c: lista){
		//	System.out.println("Nome :"+c.getClassname()+"= - number: "+c.getNumberOfInstance());
		//}
		
		testTemplateSelector(words);
		
		
		/*
		List<CandidateClass> lista = new java.util.ArrayList<CandidateClass>();
		CandidateClass c1= new CandidateClass("pessao",2);
		CandidateClass c2= new CandidateClass("pessao",2);
		
		CandidateClass c3 = new CandidateClass("pessao",2);
		
		c1.setClassname("pessoa");
		c1.setNumberOfInstance(1);
		c2.setClassname("coisa");
		c2.setNumberOfInstance(2);
		
		c3.setClassname("coisaaa");
		c3.setNumberOfInstance(4);
		lista.add(c2);
		lista.add(c1);
		
		lista.add(c3);
		
		
		for (CandidateClass c: lista){
			System.out.println("Nome :"+c.getClassname()+" - number: "+c.getNumberOfInstance());
		}
		Collections.sort(lista);
		System.out.println("---------------");
		for (CandidateClass c: lista){
			System.out.println("Nome :"+c.getClassname()+" - number: "+c.getNumberOfInstance());
		}
		
		SortedSet<CandidateClass> setC= new TreeSet<CandidateClass>();
		
		setC.add(c1);
		setC.add(c2);
		
		System.out.println("Size..."+setC.size());
		
		
		TreeSet<String> ts1 = new TreeSet<String>(); 
		  
        // Elements are added using add() method 
        ts1.add("A"); 
        ts1.add("B"); 
        ts1.add("C"); 
  
        // Duplicates will not get insert 
        ts1.add("D"); 
  
        // Elements get stored in default natural 
        // Sorting Order(Ascending) 
        System.out.println(ts1.size()); 
        
		*/
		
		
    	System.out.println("End ...: "+dtf.format(LocalDateTime.now()) );
	
    }

	/**
	 * @param words
	 */
	public static void testTemplateSelector(String[] words) {
		TemplatesSearcher templateSelector = new TemplatesSearcher();
		
		/*Set<Template> templates = templateSelector.selectTemplates(words) ;
		
		int i=0;
		
		System.out.println("---------------------------------------------\n"
		+ "Templates Selector :");
 
		if (templates!=null){
			for (Template template: templates){
				
				System.out.println("Template "+(++i)+" ="+ template.getTitle());
		 
			}
		}
		*/
		
		List<TemplateCandidate> templatesCand = 
				templateSelector.selectTemplateCandidates(words) ;
		
		int i=0;
		
		
		System.out.println("---------------------------------------------\n"
		+ "Templates Selector :");
		
		TemplateMappingManager tmManager = new TemplateMappingManager();
 
		if (templatesCand!=null){
			for (TemplateCandidate templateCand: templatesCand){
				
				System.out.println("Id ="+templateCand.getId()+" - Template "+(++i)+" ="
				+ templateCand.getTitle()+ " - Ocorrências ="
						+templateCand.getFrequence());
				
				int id=templateCand.getId();
				System.out.println("Template id ="+id);
				
				TemplateMapping templateMapping = 
						tmManager.getTemplateMappingByTemplateId(
						id);
				
				if(templateMapping==null){
					return;
				}
				Set<String> properties = templateMapping.getAtributesMappings().keySet();
				
				int k=0;
				for(String property: properties){
					System.out.print(++k+"- "+property+"\t");
				}
				System.out.println("\n");
		 
			}
		}

	}

	/**
	 * 
	 */
	private static void test_isSubclassOfClass(String parentClass, String childClass) {
		String[] dbo_classes={"dbo:Politician", "dbo:City", "dbo:Settlement",
				"dbo:AdministrativeRegion", "dbo:Person", "dbo:Agent" };
        
		
		OntModel ontModel= DBpediaOntology.getOntologyModel();
    	
		//obter as uri
    	String parentClassUri=getDboIRI(parentClass);
    	String childClassUri=getDboIRI(childClass);
		
    	OntClass parentOntClass=ontModel.getOntClass(parentClassUri);
    	OntClass childOntClass = ontModel.getOntClass(childClassUri);
    	
    	System.out.println("is  a "+childClass+ " direct subclasse of "+parentClass
    			+ "? "+parentOntClass.hasSubClass(childOntClass,true) );
    	
    	System.out.println("is  an "+childClass+ " indirect subclasse of "+parentClass
    			+ "? "+parentOntClass.hasSubClass(childOntClass,false) );
	}

	/**
	 * Obter IRI da classe/propriedade
	 * @param parentClass: nome curto do conceito
	 * @return
	 */
	public static String getDboIRI(String concept) {
		return "http://dbpedia.org/ontology/"+concept;
	}


}
