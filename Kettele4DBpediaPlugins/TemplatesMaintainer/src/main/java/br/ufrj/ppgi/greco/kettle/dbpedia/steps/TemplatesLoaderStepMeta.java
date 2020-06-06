package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * Recupera os templates da Wikipedia.
 * Os mapeamentos são interpretados.
 * @author Jean Gabriel Nguema Ngomo
 */
@Step(
		  id = "TemplatesMaintainer",
		  name = "Templates Maintainer",
		  description = "Recupera os templates e realiza sua persistência",
		  //image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.svg",
		  image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.jpg",  
		  categoryDescription = "LinkedDataBR",
		  documentationUrl = "https://github.com/jgnn/TemplatesLoader.git"
		)
public class TemplatesLoaderStepMeta extends BaseStepMeta implements StepMetaInterface {

	// Fields for serialization
	public enum Field {
		TEMPLATE_OUT_FIELD,
		TEMPLATE_PATTERN_IN
		
	}

	//output
	private String infoboxTemplateOutputField;
	
	//input
	private String templatePattern;
		
	
	public TemplatesLoaderStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {

		// if (Const.isEmpty(fieldName)) {
		// CheckResultInterface error = new CheckResult(
		// CheckResult.TYPE_RESULT_ERROR,
		// "error rorororroroo",
		// stepMeta);
		// remarks.add(error);
		// }
		// else {
		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);
		// }

	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new TemplatesLoaderStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new TemplatesLoaderStepData();
	}

	@Override
	public String getDialogClassName() {
		return TemplatesLoaderStepDialog.class.getName();
	}

	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		
		try {
		
			
			infoboxTemplateOutputField = XMLHandler.getTagValue(stepDomNode, 
					Field.TEMPLATE_OUT_FIELD.name());
			
			templatePattern = XMLHandler.getTagValue(stepDomNode, 
					Field.TEMPLATE_PATTERN_IN.name());
			
			
			
			
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getXML() throws KettleException {
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XMLHandler.addTagValue(Field.TEMPLATE_OUT_FIELD.name(), 
				infoboxTemplateOutputField));
		
		xml.append(XMLHandler.addTagValue(Field.TEMPLATE_PATTERN_IN.name(), 
				templatePattern));
		
		return xml.toString();
	}

	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		
		
		infoboxTemplateOutputField = repository.getStepAttributeString(stepIdInRepository, 
				Field.TEMPLATE_OUT_FIELD.name());
		
		templatePattern = repository.getStepAttributeString(stepIdInRepository, 
				Field.TEMPLATE_PATTERN_IN.name());
		
		
	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {


		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.TEMPLATE_OUT_FIELD.name(), infoboxTemplateOutputField);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.TEMPLATE_PATTERN_IN.name(), templatePattern);
	}

	
	public void setDefault() {
		
		//Valores default
		infoboxTemplateOutputField = "Template";
		templatePattern="Info/";
	}

	// Para os campos Field.OUT_*, refere-se ao tipo dos campos cujos nomes sao
	// especificados pelas estas variaveis desta classe Meta
	public int getFieldType(Field field) {
	
		return ValueMetaInterface.TYPE_STRING;
	}

	/**
	 * Cria o field com o tipo especifico.
	 * @param name
	 * @param field
	 * @return
	 */
	public ValueMetaInterface getValueMeta(String name, Field field) {
		
		if (field == Field.TEMPLATE_OUT_FIELD)
			return new ValueMetaString(name);
		
		return new ValueMetaString(name);
	}

	/**
	 * it describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		
		//Limpar.
		//inputRowMeta.clear();

		ValueMetaInterface field = null;

		field = getValueMeta(infoboxTemplateOutputField, Field.TEMPLATE_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
		
	}

	
	// setter and getter
	

	public String getInfoboxTemplateOutputField() {
		return infoboxTemplateOutputField;
	}

	public void setInfoboxTemplateOutputField(String infoboxTemplateOutputField) {
		this.infoboxTemplateOutputField = infoboxTemplateOutputField;
	}

	public String getTemplatePattern() {
		return templatePattern;
	}

	public void setTemplatePattern(String templatePattern) {
		this.templatePattern = templatePattern;
	}

}
