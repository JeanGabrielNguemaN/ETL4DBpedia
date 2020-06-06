package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.ufrj.ppgi.greco.kettle.dbpedia.templates.TemplateCandidate;

public class TemplateSelectorStepData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	
	public int templateFieldIndex=-1;

	public List<TemplateCandidate> templates=null; 
}
