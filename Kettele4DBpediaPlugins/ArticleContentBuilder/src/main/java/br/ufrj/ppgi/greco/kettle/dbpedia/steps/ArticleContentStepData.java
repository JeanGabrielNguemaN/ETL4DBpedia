package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;



public class ArticleContentStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;


	public HashMap<String, Integer> fieldsIndexesHashMap;


	
	public int[] inputFieldIndex=null;
	public String mappings=null;
	public String mappingsField="templateMappings";
	public String titleField=null;
	public String firstSectionField=null;
	//public String infoboxName=null;


	public String infoboxName;

}
