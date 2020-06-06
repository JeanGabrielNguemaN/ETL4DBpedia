package br.ufrj.ppgi.greco.kettle.dbpedia.steps;


import java.util.HashSet;
import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

//import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template.*;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class TemplatesLoaderStepData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;

	public String prefixUrlTemplate=null;
	public String fieldForIntroduction=null;
	public String infoboxName=null;

	public String prefixUrlMappingsDoTemplate=null;

	public List<Template> infoboxTemplates=null;

	public HashSet<String> populatedTemplates=null;

}
