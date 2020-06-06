package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TemplateManager  templateManager= new TemplateManager();
		
		Template template= new Template("Info/Template 3", null);
		templateManager.instert(template);
		

	}

}
