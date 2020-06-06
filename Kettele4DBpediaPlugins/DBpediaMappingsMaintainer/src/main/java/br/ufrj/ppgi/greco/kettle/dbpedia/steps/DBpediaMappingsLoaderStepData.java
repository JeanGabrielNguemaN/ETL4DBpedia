package br.ufrj.ppgi.greco.kettle.dbpedia.steps;


import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

//import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template.*;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class DBpediaMappingsLoaderStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;

	public String templateName=null;

	public HashMap<String, Integer> fieldIndexHashMap=null;

	public int indexOfMappingsStauts=-1;

}
