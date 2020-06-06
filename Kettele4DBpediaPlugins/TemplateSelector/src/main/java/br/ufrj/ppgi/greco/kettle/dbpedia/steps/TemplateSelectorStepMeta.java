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


@Step(
		id = "TemplateSelector",
		name = "Template Selector",
		description = "Seleciona o template informando palavras chaves",
		image ="br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.svg",
		categoryDescription = "LinkedDataBR",
		documentationUrl = "https://github.com/jgnn/TemplateSelector.git"
		)

public class TemplateSelectorStepMeta extends BaseStepMeta implements StepMetaInterface {
	
	/**
	  *  The PKG member is used when looking up internationalized strings.
	  *  The properties file with localized keys is expected to reside in 
	  *  {the package of the class specified}/messages/messages_{locale}.properties   
	  */
	
	// Fields for serialization
	public enum Field {
		VERSION,
		// Aba 'Campos de saida'
		TEMPLATE_OUTPUT,
		KEYCONCEPTS_INPUT
	}


	private String templateFieldName;

	private String keyConcepts;

	//private String endpointUri;
	// private String defaultGraph;
	// private DataTable <String> prefixes;

	

	public TemplateSelectorStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {

		 //if (Const.isEmpty(keyWords)) {
		// CheckResultInterface error = new CheckResult(
		// CheckResult.TYPE_RESULT_ERROR,
		// "error",
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
		return new TemplateSelectorStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new TemplateSelectorStepData();
	}

	@Override
	public String getDialogClassName() {
		return TemplateSelectorStepDialog.class.getName();
	}

    // Carregar campos a partir do XML de um .ktr
	//@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {

		try {
			

			templateFieldName = (String) XMLHandler.getTagValue(stepDomNode, Field.TEMPLATE_OUTPUT.name());
			keyConcepts = (String) XMLHandler.getTagValue(stepDomNode, Field.KEYCONCEPTS_INPUT.name());

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XMLHandler.addTagValue(Field.TEMPLATE_OUTPUT.name(), templateFieldName));
		
		xml.append(XMLHandler.addTagValue(Field.KEYCONCEPTS_INPUT.name(), keyConcepts));


		return xml.toString();
	}

	// Carregar campos a partir do repositorio
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		
		try {
			int version = (int) repository.getStepAttributeInteger(stepIdInRepository, Field.VERSION.name());

			switch (version) {
			case 1:
				
				templateFieldName=repository.getStepAttributeString(stepIdInRepository,
						Field.TEMPLATE_OUTPUT.name());
				
				keyConcepts=repository.getStepAttributeString(stepIdInRepository,
						Field.KEYCONCEPTS_INPUT.name());
				
				break;
			default:
				setDefault();
			}
		} catch (Exception e) {
			throw new KettleException(
					"Unable to read step information from the repository for id_step=" + stepIdInRepository, e);
		}

	}

	// Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {
		try {
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.VERSION.name(), 1);

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.TEMPLATE_OUTPUT.name(),
					templateFieldName);
			
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.KEYCONCEPTS_INPUT.name(),
					keyConcepts);
	
		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + idOfStep, e);
		}
	}

	/**
	 * Inicializar os campos para nao dexa-los nulos
	 */
	public void setDefault() {
      /*
		mapTable = new DataTable<String>(Field.MAP_TABLE.name(), 
				Field.MAP_TABLE_SUBJECT_FIELD_NAME.name(),
				Field.MAP_TABLE_PREDICATE_FIELD_NAME.name(), 
				Field.MAP_TABLE_PREDICATE_URI.name(),
				Field.MAP_TABLE_OBJECT_FIELD_NAME.name(), 
				//acrescentei
				Field.MAP_TABLE_SUBJECT_URI.name());

		subjectOutputFieldName = "campoDominio";
		predicateOutputFieldName = "atributoInfobox";
		//objectOutputFieldName = "object";
		keepInputFields = false;
		*/
		templateFieldName = "template";
		keyConcepts="";
	}

	/**
	 * Descrever os campos de saída de cada registro (row) 
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		
		
		ValueMetaInterface field = null;
		
		field = new ValueMetaString(templateFieldName);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		/*
		if (!keepInputFields)
			inputRowMeta.clear();

		field = new ValueMetaString(subjectOutputFieldName);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = new ValueMetaString(predicateOutputFieldName);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
        */
		
		//field = new ValueMetaString(objectOutputFieldName);
		//field.setOrigin(name);
		//inputRowMeta.addValueMeta(field);
			
	}

	// Getters & Setters
	public String getTemplateFieldName() {
		return templateFieldName;
	}

	public void setTemplateFieldName(String templateFieldName) {
		this.templateFieldName = templateFieldName;
	}

	public String getKeyConcepts() {
		return keyConcepts;
	}

	public void setKeyConcepts(String keyConcepts) {
		this.keyConcepts = keyConcepts;
	}
	
}
