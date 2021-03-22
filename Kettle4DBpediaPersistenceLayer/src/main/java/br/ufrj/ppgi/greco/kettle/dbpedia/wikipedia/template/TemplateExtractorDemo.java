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

package br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import java.util.ArrayList;

public class TemplateExtractorDemo {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TemplateExtractor extractor= new TemplateExtractor();
		String pattern="Info/Taxonomia";
		
		//System.out.println(demo.searchByName(srsearch, srnamespace, srlimit, sroffset));
		
		//Obter todos os templates que começam "/Info", sem exceção
		ArrayList<Template> templates= extractor.getAllTemplates(pattern);
		
		
		
		//Dentro dos templates, recuerar apenas apenas que sao de Infobox
		templates= extractor.getInfoboxTemplates(templates);
		
		System.out.println("Infobox template Size = "+templates.size());
		
		System.out.println("All template Size = "+templates.size());
		
		printArrayList(templates);
		
		//processCategories(demo, templates);
		
		/*
		String str="Predefinição:Info/Produto botânico/doc";
		String[] tokens= str.split("Info/");
		System.out.println("tokens = "+tokens[0]);
		*/
		
	}

	
	public static void printArrayList(ArrayList<Template> templates) {
		
		for(Template template: templates){
			System.out.println(template.getTitle());
		}
	}
	
}
