/*
* %W% %E% Jean Gabriel Nguema Ngomo
*
* Copyright 2021 Jean Gabriel Nguema Ngomo
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
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
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaInteger;
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

import br.ufrj.ppgi.greco.kettle.dbpedia.steps.ArticleContentBuilderStepMeta.Field;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

@Step(
		  id = "ArticleContentBuilder",
		  name = "Article Content Builder",
		  description = "Verifica se um infobox existe na Wikipedia",
		  //image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.svg",
		  image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.jpg",
		  categoryDescription = "LinkedDataBR",
		  documentationUrl = "https://github.com/jgnn/Article Content Builder.git"
		)
public class ArticleContentBuilderStepMeta extends BaseStepMeta implements StepMetaInterface {

	// Fields for serialization
	public enum Field {
		SELECTED_PROPERTY_FOR_TITLE, 
		FIRST_SECTION_FIELD, 
		TITLE_IN_INFO_COMPARISON, 
		INFOBOX_OUT_FIELD, 
		TITLE_OUT_FIELD,
		FIRST_SECTION_OUT_FIELD,
		//MAP_TABLE,
		//MAP_TABLE_MAPPED_PROPERTY_FIELD_NAME
	}

	//Values - tipo refere-se ao tipo destas variaveis
	
	//private DataTable<String> mapTable;
	//private Boolean titleUsedInInfoboxComparison;
	
	private String titleField;
	private String firstSectionField;
	private String infoboxContentOutput;

	private String firstSectionOutput;
	
	public ArticleContentBuilderStepMeta() {
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
		return new ArticleContentStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new ArticleContentStepData();
	}

	@Override
	public String getDialogClassName() {
		return ArticleContentStepDialog.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		
		
		try {
		
			titleField = XMLHandler.getTagValue(stepDomNode, 
					Field.SELECTED_PROPERTY_FOR_TITLE.name());
			
			firstSectionField = XMLHandler.getTagValue(stepDomNode, 
					Field.FIRST_SECTION_FIELD.name());
			
			infoboxContentOutput = XMLHandler.getTagValue(stepDomNode, 
					Field.INFOBOX_OUT_FIELD.name());
			
			firstSectionOutput = XMLHandler.getTagValue(stepDomNode, 
					Field.FIRST_SECTION_OUT_FIELD.name());
			
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getXML() throws KettleException {
		
		
		StringBuilder xml = new StringBuilder();
	
		xml.append(XMLHandler.addTagValue(Field.SELECTED_PROPERTY_FOR_TITLE.name(), titleField));
		
		xml.append(XMLHandler.addTagValue(Field.FIRST_SECTION_FIELD.name(), firstSectionField));
		
		
		xml.append(XMLHandler.addTagValue(Field.INFOBOX_OUT_FIELD.name(), infoboxContentOutput));
		
		xml.append(XMLHandler.addTagValue(Field.FIRST_SECTION_OUT_FIELD.name(), firstSectionOutput));
		
		return xml.toString();
	}

	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		
	
		titleField = repository.getStepAttributeString(stepIdInRepository, 
				Field.SELECTED_PROPERTY_FOR_TITLE.name());
		
		firstSectionField = repository.getStepAttributeString(stepIdInRepository, 
				Field.FIRST_SECTION_FIELD.name());
		
		
		infoboxContentOutput = repository.getStepAttributeString(stepIdInRepository, 
				Field.INFOBOX_OUT_FIELD.name());
	
		firstSectionOutput = repository.getStepAttributeString(stepIdInRepository, 
				Field.FIRST_SECTION_OUT_FIELD.name());
		
	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SELECTED_PROPERTY_FOR_TITLE.name(), titleField);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.FIRST_SECTION_FIELD.name(), firstSectionField);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.INFOBOX_OUT_FIELD.name(), infoboxContentOutput);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.FIRST_SECTION_OUT_FIELD.name(), firstSectionOutput);
	}

	public void setDefault() {
		//User inputs in dialog
		firstSectionField = "";
		titleField = "";
	
		//Output fields
		infoboxContentOutput = "infoboxContent"; 
		firstSectionOutput="firstSection";
	}

	// Para os campos Field.OUT_*, refere-se ao tipo dos campos cujos nomes sao
	// especificados pelas estas variaveis desta classe Meta
	public int getFieldType(Field field) {
		//if (field == Field.PORT)
		//	return ValueMetaInterface.TYPE_INTEGER;
		if (field == Field.INFOBOX_OUT_FIELD)
			//return ValueMetaInterface.TYPE_INTEGER;
			return ValueMetaInterface.TYPE_STRING;
		
		//if (field == Field.PAGE_EXISTS_OUT_FIELD)
			//return ValueMetaInterface.TYPE_INTEGER;
		//	return ValueMetaInterface.TYPE_BOOLEAN;
		
		if (field == Field.TITLE_IN_INFO_COMPARISON)
			return ValueMetaInterface.TYPE_BOOLEAN;
		
		return ValueMetaInterface.TYPE_STRING;
	}

	/**
	 * Cria o field com o tipo especifico.
	 * @param name
	 * @param field
	 * @return
	 */
	public ValueMetaInterface getValueMeta(String name, Field field) {
		//if (field == Field.PORT)
		//	return new ValueMetaInteger(name);
		if (field == Field.INFOBOX_OUT_FIELD)
			return new ValueMetaString(name);
		
		if (field == Field.TITLE_IN_INFO_COMPARISON)
			return new ValueMetaBoolean(name);
		
		//if (field == Field.PAGE_EXISTS_OUT_FIELD)
		//	return new ValueMetaBoolean(name);
		
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
		
		String[] fieldnames = inputRowMeta.getFieldNames();
		
		//Remover todos os campos de entrada, menos o título do artigo e 
		// e a informação de sua existência
		for(String field: fieldnames) {
			if(!(field.equals("articleExists") 
					|| field.equals("articleTitle")
					|| field.equals("potentialArticleTitle"))){
				try {
					inputRowMeta.removeValueMeta(field);
					
				} catch (KettleValueException e) {
					// TODO Auto-generated catch block
					logBasic("==== Exception removing the field ("+field+" ) "
							+ " : "+e.getMessage());
				}
			}
		}

		ValueMetaInterface field = null;

		//field = getValueMeta(titleOutput, Field.TITLE_OUT_FIELD);
		//field.setOrigin(name);
		//inputRowMeta.addValueMeta(field);
			
		field = getValueMeta(infoboxContentOutput, Field.INFOBOX_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
		
		field = getValueMeta(firstSectionOutput, Field.FIRST_SECTION_OUT_FIELD);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
	}

	// Getters & Setters

	public String getTitleField() {
		return titleField;
	}

	public void setTitleField(String value) {
		titleField = value;
	}

	public String getFirstSectionField() {
		return firstSectionField;
	}

	public void setFirstSectionField(String fieldForFirstSection) {
		this.firstSectionField = fieldForFirstSection;
	}

	
	public String getInfoboxContentOutput() {
		return infoboxContentOutput;
	}

	public void setInfoboxContentOutput(String wikipediatitle) {
		this.infoboxContentOutput = wikipediatitle;
	}

	

	/*
	public String getTitleOutput() {
		return titleOutput;
	}

	public void setTitleOutput(String titleOutput) {
		this.titleOutput = titleOutput;
	}
	*/
	public String getFirstSectionOutput() {
		return firstSectionOutput;
	}

	public void setFirstSectionOutput(String firstSectionOutput) {
		this.firstSectionOutput = firstSectionOutput;
	}
}
