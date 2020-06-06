package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.demo;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.ComplexDeclaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Declaration;

public class DemoComplexDeclaration {
	
	public static void main(String[] args) {
		
		ComplexDeclaration  mappingAtor=  new ComplexDeclaration("TemplateMapping");
		
		mappingAtor.add("mapToClass","Actor");
		
		List<Declaration> declarations= new ArrayList<Declaration>();
		
		
		//Elemento 1
		String templatePropertyValue = "nome";
		String ontologyPropertyValue = "foaf:name";
		
		ComplexDeclaration propertyMapping = generatePropertyMappingDeclaration(templatePropertyValue,
				ontologyPropertyValue);
		
		declarations.add(propertyMapping);
		
		//Elemento 2
		propertyMapping = generatePropertyMappingDeclaration("nascimento_data",
				"birthDate");
		
		declarations.add(propertyMapping);
		
		mappingAtor.add("mappings",declarations);
		
		mappingAtor.print();
		
	}

	/**
	 * @param templatePropertyValue
	 * @param ontologyPropertyValue
	 * @return
	 */
	public static ComplexDeclaration generatePropertyMappingDeclaration(String templatePropertyValue,
			String ontologyPropertyValue) {
		ComplexDeclaration propertyMapping= new ComplexDeclaration("PropertyMapping");
		
		
		propertyMapping.add("templateProperty", templatePropertyValue);
	
		propertyMapping.add("ontologyProperty", ontologyPropertyValue);
		return propertyMapping;
	}

}
