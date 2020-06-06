package br.ufrj.ppgi.greco.kettle.dbpedia.publication.steps;

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
import org.pentaho.di.core.row.value.ValueMetaBoolean;
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.ufrj.ppgi.greco.kettle.dbpedia.publication.steps.ArticleCheckerStepMeta.Field;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

@Step(
		  id = "ArticleChecker",
		  name = "Article Checker",
		  description = "Verifica se um Artigo existe na Wikipedia",
		  image = "br/ufrj/ppgi/greco/kettle/dbpedia/publication/steps/resources/image.png",
		  categoryDescription = "LinkedDataBR",
		  documentationUrl = "https://github.com/jgnn/ArticleChecker.git"
		)
public class ArticleCheckerStepMeta extends BaseStepMeta implements StepMetaInterface {

	// Fields for serialization
	public enum Field {
		SELECTED_FIELD_FOR_TITLE, 
		SELECTED_PROPERTY_FOR_INFOBOX_TITLE,
		SELECTED_MAPPED_PROPERTIES, 
		TITLE_IN_INFO_COMPARISON, 
		ARTICLE_EXISTS_OUT_FIELD,
		//NOVO
		MAP_TABLE,
		MAP_TABLE_MAPPED_PROPERTY_FIELD_NAME,
		TITLE_OUT_FIELD,
		POTENTIAL_TITLE_OUT_FIELD
	}
	

	
	// Inputs Values - tipo refere-se ao tipo destas variaveis
	private String selectedFieldForTitle;
	private String selectedPropertyForInfoboxTitle;
	private Boolean titleUsedInInfoboxComparison;
	private String selectedMappedProperties;
	
	// Output - ATENCAO: tipo refere-se ao tipo dos campos cujos nomes sao
	// especificados por estas variaveis
	private String wikipediatitleOutputField;
	private String pageExistsOuputField;
	private String titleOutput;

	//Campos do step
	private DataTable<String> mapTable;

	public ArticleCheckerStepMeta() {
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
		return new ArticleCheckerStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new ArticleCheckerStepData();
	}

	@Override
	public String getDialogClassName() {
		return ArticleCheckerStepDialog.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		
		
		try {
			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());
			
			mapTable = (DataTable<String>) xs.fromXML(
					XMLHandler.getTagValue(stepDomNode, Field.MAP_TABLE.name()));
			
	
			selectedFieldForTitle = XMLHandler.getTagValue(stepDomNode, 
					Field.SELECTED_FIELD_FOR_TITLE.name());
			
			selectedPropertyForInfoboxTitle = XMLHandler.getTagValue(stepDomNode, 
					Field.SELECTED_PROPERTY_FOR_INFOBOX_TITLE.name());
			
			selectedMappedProperties = XMLHandler.getTagValue(stepDomNode, 
					Field.SELECTED_MAPPED_PROPERTIES.name());
			
			titleUsedInInfoboxComparison = "Y".equals(XMLHandler.getTagValue(stepDomNode, 
					Field.TITLE_IN_INFO_COMPARISON.name()));
			
			wikipediatitleOutputField = XMLHandler.getTagValue(stepDomNode, 
					Field.TITLE_OUT_FIELD.name());
			
			pageExistsOuputField = XMLHandler.getTagValue(stepDomNode, 
					Field.ARTICLE_EXISTS_OUT_FIELD.name());
			
			titleOutput = XMLHandler.getTagValue(stepDomNode, 
					Field.POTENTIAL_TITLE_OUT_FIELD.name());
			
		
			
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getXML() throws KettleException {
		XStream xs = new XStream(new DomDriver());
		xs.alias("DataTable", DataTable.class);
		xs.registerConverter(new DataTableConverter());
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XMLHandler.addTagValue(
				Field.MAP_TABLE.name(), xs.toXML(mapTable)));
		
		xml.append(XMLHandler.addTagValue(Field.SELECTED_FIELD_FOR_TITLE.name(), selectedFieldForTitle));
		
		xml.append(XMLHandler.addTagValue(Field
				.SELECTED_PROPERTY_FOR_INFOBOX_TITLE.name(), selectedPropertyForInfoboxTitle));
		
		xml.append(XMLHandler.addTagValue(Field.SELECTED_MAPPED_PROPERTIES.name(), selectedMappedProperties));
		
		xml.append(XMLHandler.addTagValue(Field.TITLE_IN_INFO_COMPARISON.name(), titleUsedInInfoboxComparison));
		
		xml.append(XMLHandler.addTagValue(Field.TITLE_OUT_FIELD.name(), wikipediatitleOutputField));
		xml.append(XMLHandler.addTagValue(Field.ARTICLE_EXISTS_OUT_FIELD.name(), pageExistsOuputField));
		
		xml.append(XMLHandler.addTagValue(Field.POTENTIAL_TITLE_OUT_FIELD.name(), titleOutput));
		
		return xml.toString();
	}

	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		
		
		int nrLines = (int) repository.getStepAttributeInteger(stepIdInRepository, "nr_lines");
		
		mapTable = new DataTable<String>(Field.MAP_TABLE.name(),
						Field.MAP_TABLE_MAPPED_PROPERTY_FIELD_NAME.name() 
					);
		
		
		String[] fields = mapTable.getHeader().toArray(new String[0]);
		
		for (int i = 0; i < nrLines; i++) {
			int nrfields = fields.length;
			String[] line = new String[nrfields];

			for (int f = 0; f < nrfields; f++) {
				line[f] = repository.getStepAttributeString(stepIdInRepository, i, fields[f]);
			}
			mapTable.add(line);
		}

		selectedFieldForTitle = repository.getStepAttributeString(stepIdInRepository, 
				Field.SELECTED_FIELD_FOR_TITLE.name());
		
		selectedPropertyForInfoboxTitle = repository.getStepAttributeString(stepIdInRepository, Field
				.SELECTED_PROPERTY_FOR_INFOBOX_TITLE.name());
		
		selectedMappedProperties = repository.getStepAttributeString(stepIdInRepository, Field
				.SELECTED_MAPPED_PROPERTIES.name());
		
		titleUsedInInfoboxComparison = repository.getStepAttributeBoolean(stepIdInRepository, Field
				.TITLE_IN_INFO_COMPARISON.name());
			
		wikipediatitleOutputField = repository.getStepAttributeString(stepIdInRepository, 
				Field.TITLE_OUT_FIELD.name());
		
		pageExistsOuputField = repository.getStepAttributeString(stepIdInRepository, 
				Field.ARTICLE_EXISTS_OUT_FIELD.name());
		
		titleOutput = repository.getStepAttributeString(stepIdInRepository, 
				Field.POTENTIAL_TITLE_OUT_FIELD.name());
		
	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

		
		// Map Table
		int linhas = (int) mapTable.size();
		int colunas = mapTable.getHeader().size();
		repository.saveStepAttribute(idOfTransformation, idOfStep, "nr_lines", linhas);
					
		for (int i = 0; i < linhas; i++) {
			for (int f = 0; f < colunas; f++) {
				
				repository.saveStepAttribute(idOfTransformation, 
						idOfStep, i, mapTable.getHeader().get(f),
				mapTable.getValue(i, f));
			}
		}
		
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SELECTED_FIELD_FOR_TITLE.name(), selectedFieldForTitle);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SELECTED_PROPERTY_FOR_INFOBOX_TITLE.name(), selectedPropertyForInfoboxTitle);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SELECTED_MAPPED_PROPERTIES.name(), selectedMappedProperties);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.TITLE_IN_INFO_COMPARISON.name(), titleUsedInInfoboxComparison);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.TITLE_OUT_FIELD.name(), wikipediatitleOutputField);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.ARTICLE_EXISTS_OUT_FIELD.name(), pageExistsOuputField);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.POTENTIAL_TITLE_OUT_FIELD.name(), titleOutput);
		
	}

	public void setDefault() {
		
		//Todos os campos de entrada e saída devem ser inicializados
		selectedMappedProperties = "";
		selectedFieldForTitle = "";
		selectedPropertyForInfoboxTitle="";
		titleUsedInInfoboxComparison = false;

		wikipediatitleOutputField = "articleTitle";
		pageExistsOuputField = "articleExists";
		setTitleOutput("potentialArticleTitle");
		
		mapTable = new DataTable<String>(Field.MAP_TABLE.name(), Field
							.MAP_TABLE_MAPPED_PROPERTY_FIELD_NAME.name());

	}

	// Retornar o tipo em função do campo
	public int getFieldType(Field field) {
		if (field == Field.ARTICLE_EXISTS_OUT_FIELD) {
			return ValueMetaInterface.TYPE_BOOLEAN;
		}
		
		if (field == Field.TITLE_IN_INFO_COMPARISON) {
			return ValueMetaInterface.TYPE_BOOLEAN;
		}
				
			
		return ValueMetaInterface.TYPE_STRING;
	}

	/**
	 * Cria o field com o tipo especifico.
	 * @param name
	 * @param field
	 * @return
	 */
	public ValueMetaInterface getValueMeta(String name, Field field) {
		
		if (field == Field.ARTICLE_EXISTS_OUT_FIELD)
			return new ValueMetaBoolean(name);
		
		return new ValueMetaString(name);
	}

	/**
	 * it describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {

		ValueMetaInterface field = null;

		field = getValueMeta(wikipediatitleOutputField, Field.TITLE_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = getValueMeta(pageExistsOuputField, Field.ARTICLE_EXISTS_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
		
		field = getValueMeta(titleOutput, Field.POTENTIAL_TITLE_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
	}

	// Getters & Setters
	public String getSelectedFieldForTitle() {
		return selectedFieldForTitle;
	}

	public void setSelectedFieldForTitle(String value) {
		selectedFieldForTitle = value;
	}

	public String getSelectedMappedProperties() {
		return selectedMappedProperties;
	}

	public void setSelectedMappedProperties(String selectedMappedProperties) {
		this.selectedMappedProperties = selectedMappedProperties;
	}

	public Boolean getTitleUsedInInfoboxComparison() {
		return titleUsedInInfoboxComparison;
	}

	public void setTitleUsedInInfoboxComparison(Boolean titleUsedInInfoboxComparison) {
		this.titleUsedInInfoboxComparison = titleUsedInInfoboxComparison;
	}

	public String getWikipediatitleOutputField() {
		return wikipediatitleOutputField;
	}

	public void setWikipediatitleOutputField(String wikipediatitle) {
		this.wikipediatitleOutputField = wikipediatitle;
	}

	public String getPageExistsOuputField() {
		return pageExistsOuputField;
	}

	public void setPageExistsOuputField(String pageExistsOuputField) {
		this.pageExistsOuputField = pageExistsOuputField;
	}

	public DataTable<String> getMapTable() {
		return mapTable;
	}

	public String getSelectedPropertyForInfoboxTitle() {
		return selectedPropertyForInfoboxTitle;
	}

	public void setSelectedPropertyForInfoboxTitle(String selectedPropertyForInfoboxTitle) {
		this.selectedPropertyForInfoboxTitle = selectedPropertyForInfoboxTitle;
	}
	
	public String getTitleOutput() {
		return titleOutput;
	}

	public void setTitleOutput(String titleOutput) {
		this.titleOutput = titleOutput;
	}
}
