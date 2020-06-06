package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.HashMap;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class TemplateMapperStepData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	public HashMap<String, Integer> fieldsIndexesHashMap=null;
	
	public int[] inputFieldIndex=null;
	public String mappings=null;
	public String mappingsField=null;
	public String[] mappedTemplateProperties=null;
	public Integer templateFieldIndex=-1;	
}
