package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathFactory;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;

public class Demo {

	public static void main(String[] args) throws JDOMException, IOException {
		// TODO Auto-generated method stub
		;
		
		/*String[] infos = InfoboxTemplatesManager.getInfoboxes();
		
		
		for (String info: infos) {
			System.out.println(""+info);
		}
		System.out.println(" "+InfoboxTemplatesManager.getInfoboxes()[0]);
		
		TemplateMappingManager tmm= new TemplateMappingManager();
		
		TemplateMapping tm = tmm.getTemplateMappingByTemplateByName("Info/Taxonomia");
		
		System.out.println("ID of TM "+tm.getTemplateId());
		HashMap<String, String> propsHashMap = tm.getAtributesMappings();
		Set<String> props = propsHashMap.keySet();
		
		for (String prop:props) {
			System.out.println("PROP ="+prop);
		}
		*/
		
		
		String mappings="\r\n" + 
				"<infoboxTemplate name=\"Info/Taxonomia - 22.54\">\r\n" + 
				"	<maps>\r\n" + 
				"		<map>\r\n" + 
				"			<from>MUNICIPIO</from>\r\n" + 
				"			<to>superclasse</to>\r\n" + 
				"		</map>\r\n" + 
				"		<map>\r\n" + 
				"			<from>PREFEITO</from>\r\n" + 
				"			<to>superclasse</to>\r\n" + 
				"		</map>\r\n" + 
				"	</maps>\r\n" + 
				"</infoboxTemplate>\r\n" + 
				"";
		
		//TemporyDataHandler.store("teste.xml", mappings);
		
		
		
		//obterMapeamentosCampoPropriedade("teste.xml",mappings);
		

		
	}

	
private static HashMap<String, String> obterMapeamentosCampoPropriedade(String filename,String mappings) throws JDOMException, IOException {
		
		File xmlSource = new File(filename);
		
		// read the XML into a JDOM2 document.
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(xmlSource);
        
        
       // Document jdomDocument = jdomBuilder.;
        
        
		
        // use the default implementation
        XPathFactory xFactory = XPathFactory.instance();
        // System.out.println(xFactory.getClass());
        
     
        Element rootNode = jdomDocument.getRootElement();
        
        Element maps = rootNode.getChild("maps");
		
       
		List<Element> mapsList =  maps.getChildren("map");
		
		
		int qde_maps=mapsList.size();
		
		System.out.println("Quantidade de Parlamentares = "+qde_maps);

		for (int i = 0; i < qde_maps; i++) {
			
		
		  //nome senador mapear e extrair

		   Element map = (Element) mapsList.get(i);
		   
		   Element from=map.getChild("from");
		   String campoDeDominio = from.getText();
		  
		   Element to=map.getChild("to");
		   String  propriedade = to.getText();

		  
		   System.out.println("Campo :..................." + campoDeDominio);
		   System.out.println("Propriedade:..." + propriedade);

		}
		return null;
	}

}
