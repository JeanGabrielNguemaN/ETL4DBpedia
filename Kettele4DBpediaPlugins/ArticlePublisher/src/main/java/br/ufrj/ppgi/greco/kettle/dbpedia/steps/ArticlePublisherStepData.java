package br.ufrj.ppgi.greco.kettle.dbpedia.steps;


import java.util.HashMap;
import java.util.HashSet;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.ContentPublisher;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.Entity;



public class ArticlePublisherStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;

	public int inputRowSize;

	public String properttyForTitle=null;
	
	public boolean titleUsedInInfoboxComparison=false;
	
	//propriedades separadas por virgulas
	public String[] selectedMappedProperties=null;

	public HashMap<String, Integer> fieldsIndexesHashMap;

	public HashSet<Entity> entities=null;
	
	
	//NOVO
	public ContentPublisher publisher=null;
	
	
	//quantidade de edições por minuno 
	// 3 para test, e 5 para produção
	int epm=3;



	public String summary=null;

	public String referenceUrl=null;

	public String referenceTitle=null;

	public String referencePublisher=null;

	public String referenceAccessdate=null;

	public String referenceAuthor=null;

	public String referenceDate=null;

	public boolean isTestPhase=true;
	
		
}
