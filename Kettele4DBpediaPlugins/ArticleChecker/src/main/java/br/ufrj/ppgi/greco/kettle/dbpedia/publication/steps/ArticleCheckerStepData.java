package br.ufrj.ppgi.greco.kettle.dbpedia.publication.steps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.Entity;
import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.EntityManager;



public class ArticleCheckerStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;

	public int inputRowSize;

	public String properttyForTitle=null;
	
	public boolean titleUsedInInfoboxComparison=false;
	
	//propriedades separadas por virgulas
	public String[] selectedMappedProperties=null;

	public HashMap<String, Integer> fieldsIndexesHashMap;

	
	public String proposedTitleField=null; 

	public String proposedInfoboxTitleProperty;

	public String mappings;

	public String infoboxName;

	//Título do infobox será usado nas comparações?
	public Boolean infoxTitleUsedInComparision=false;

	public EntityManager entityManager=null;
	
}
