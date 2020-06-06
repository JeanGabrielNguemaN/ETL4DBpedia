package br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter;

import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public interface IDeclaration {

	public void print();
	
	public Object interpret(Template template);
}
