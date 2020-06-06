package br.ufrj.ppgi.greco.kettle.dbpedia.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Demo {

	public static void main(String[] args) throws Exception {
	
		testXML();
		
		String title_="AABAA aggagaga ";
		
		System.out.println(""+title_.trim().replace(" ", "%20"));
		
		
	}

	private static void testXML() throws ParserConfigurationException, SAXException, IOException {
		String xmlString = "<infoboxTemplate name=\"Info/Taxonomia\">\r\n" + 
				"	<maps>\r\n" + 
				"		<map>\r\n" + 
				"			<from>scientificname</from>\r\n" + 
				"			<to>nome</to>\r\n" + 
				"		</map>\r\n" + 
				"		<map>\r\n" + 
				"			<from>kingdom</from>\r\n" + 
				"			<to>reino</to>\r\n" + 
				"		</map>\r\n" + 
				"		<map>\r\n" + 
				"			<from>family</from>\r\n" + 
				"			<to>família</to>\r\n" + 
				"		</map>\r\n" + 
				"		<map>\r\n" + 
				"			<from>genus</from>\r\n" + 
				"			<to>genero</to>\r\n" + 
				"		</map>\r\n" + 
				"		<map>\r\n" + 
				"			<from>species</from>\r\n" + 
				"			<to>espécie</to>\r\n" + 
				"		</map>\r\n" + 
				"	</maps>\r\n" + 
				"</infoboxTemplate>";

		    DocumentBuilder docBuilder = 
		    		DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    
		    InputSource inputSource = new InputSource();
		    
		    inputSource.setCharacterStream(new StringReader(xmlString));

		    org.w3c.dom.Document doc = docBuilder.parse(inputSource);
		   
		    NodeList nodes = doc.getElementsByTagName("infoboxTemplate");
		    
		    org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(0);
		    
		    Attr attributo = element.getAttributeNode("name");
		    System.out.println(attributo.getTextContent());
	}

	private static void testeDatas() {
		System.out.println("1220-11".split("[-]")[0]);
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		Date now = new Date();
		
	
		System.out.println(year);
	}
	

}
