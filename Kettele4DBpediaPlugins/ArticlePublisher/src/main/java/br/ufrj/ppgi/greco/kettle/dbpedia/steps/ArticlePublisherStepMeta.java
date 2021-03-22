/*
 * Copyright 2020 Jean Gabriel Nguema Ngomo

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pentaho.database.util.Const;
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
import org.pentaho.di.i18n.BaseMessages;
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

import br.ufrj.ppgi.greco.kettle.dbpedia.steps.ArticlePublisherStepMeta.Field;

/**
* This class has the following responsibilities: 
*  
* 		1. Maintain step settings
* 		2. Validate step settings
* 		3. Serialize step settings
* 		4. Provide access to step classes
* 		5. Perform row layout changes
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
@Step(
		  id = "ArticlePublisher",
		  name = "Article Publisher",
		  description = "Publica Artigo na DBpedia",
		  image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.jpg",
		  categoryDescription = "LinkedDataBR",
		  documentationUrl = "https://github.com/jgnn/ArticlePublisher.git"
		)
public class ArticlePublisherStepMeta extends BaseStepMeta implements StepMetaInterface {

	
	private static Class<?> PKG = ArticlePublisherStepMeta.class;

	// Fields for serialization
	public enum Field {
		ENDPOINT_IN,
		BOT_USER_IN,
		BOT_PASSWAORD_IN,
		SUMMARY_IN,
		
		EPM_IN,
		TEST_PHASE_IN,
		CREATE_ONLY_IN,
		
		SERVER_RESPONSE_OUT, 
		SUCCES_OUT,
		//References info
		REF_URL_IN,
		REF_TITLE_IN,
		REF_DATE_IN,
		REF_AUTHOR_IN,
		REF_PUBLISHER_IN,
		REF_ACCESS_DATE_IN,
		//Categoria do Artigo
		CATEGORIES_IN
	}

	//Campos do step
	//Input
	private String endPoint;
	private String botUser;
	private String botPassword;
	private String summary;
	//quantidade de edição por minuto: até 3 para testes e até 5 para produção.
	private String epm;
	Boolean testPhase=true; 
	
	private String categories;
	Boolean createOnly;
	
	//Reference
	String refUrl;
	String refTitle;
	String refAuthor;
    String refPublisher;
	String refDate;
	String refAccessDate; 
	
	//Ouput
	private String serverResponseOutputField;
	private String successOuputField;
	

	public ArticlePublisherStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
	
		
		if (Const.isEmpty(refUrl)) {
			CheckResultInterface error = new CheckResult(
			//CheckResult.TYPE_RESULT_ERROR,"URL de  REFERENCIA VAZIA",stepMeta);
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.Url"), stepMeta);
			
			remarks.add(error);
			
		}else if (Const.isEmpty(refPublisher)) {
			CheckResultInterface error = new CheckResult(
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.Publisher"), stepMeta);
			
			remarks.add(error);
			
		}else if (Const.isEmpty(refAccessDate)) {
			CheckResultInterface error = new CheckResult(
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.AccessDate"), stepMeta);
			
			remarks.add(error);
			
		}else if (Const.isEmpty(refTitle)) {
			CheckResultInterface error = new CheckResult(
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.Title"), stepMeta);
			
			remarks.add(error);
		} else if (!validDates()) {
			CheckResultInterface error = new CheckResult(
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.DateFormat"), stepMeta);
			
			remarks.add(error);
		} else if (!validEmp()) {
			CheckResultInterface error = new CheckResult(
			CheckResult.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "ArticlePubliserStep.Error.EPM"), stepMeta);
			
			remarks.add(error);
		} else {
			CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
			remarks.add(ok);
		
		}

	}

	private boolean validEmp() {
		if(epm.equals("")) {
			
			return false;
		}
		
		int  number =0;
		
		try{
			number = Integer.parseInt(epm);
		 
		} catch (NumberFormatException nfe) {
		        return false;
		}
		//não pode ser nulo
		if(number==0) {
			return false;
		}
		//Na fase de teste: até 3 edições
		if(testPhase && (number > 3 )) {
			return false;
		}
		
		//Nas outras fases: até 5 edições
		if( number > 5) {
			return false;
		}
		return true;
	}

	/**
	 * Validar formato das datas
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean validDates() {
		
		boolean valid=validDate(refAccessDate);
		if(!valid) {
			return valid;
		}
		//refDate não é obrigatorio
		if(refDate==null || refDate.trim().equals("")) {
			return true;
		}
		
		return validDate(refDate);
	}

	private boolean validDate(String data) {
		if(data==null) {
			return false;
		}
	
		String[] tokens = data.trim().split("-");
		
		if(tokens.length!=3) {
			return false;
		}
		int ano = 0;
		int mes =0;
		int dia  =0;
		
		try{
			ano = Integer.parseInt(tokens[0]);
			mes = Integer.parseInt(tokens[1]);
			dia  = Integer.parseInt(tokens[2]);
		 
		} catch (NumberFormatException nfe) {
		        return false;
		}
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		int currentMonth=Calendar.getInstance().get(Calendar.MONTH);
		
		int currentDayOfMonth=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		
		
		if(dia>31 || mes>12 || ano> currentYear) {
			return false;
		}
		
		if(ano==currentYear) {
			if (mes>currentMonth)
				return false;
			if(mes==currentMonth && dia>currentDayOfMonth )
				return false;
		}
	
		return true;
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new ArticlePublisherStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new ArticlePublisherStepData();
	}

	@Override
	public String getDialogClassName() {
		return ArticlePublisherStepDialog.class.getName();
	}

	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		
		try {
			endPoint = XMLHandler.getTagValue(stepDomNode, 
					Field.ENDPOINT_IN.name());
			
			botUser = XMLHandler.getTagValue(stepDomNode, 
					Field.BOT_USER_IN.name());
			botPassword = XMLHandler.getTagValue(stepDomNode, 
					Field.BOT_PASSWAORD_IN.name());
			summary = XMLHandler.getTagValue(stepDomNode, 
					Field.SUMMARY_IN.name());
			
			
			
			epm = XMLHandler.getTagValue(stepDomNode, 
					Field.EPM_IN.name());
			testPhase = "Y".equals(XMLHandler.getTagValue(stepDomNode, 
					Field.TEST_PHASE_IN.name()));
			
			refUrl = XMLHandler.getTagValue(stepDomNode, 
					Field.REF_URL_IN.name());
			
			refTitle=XMLHandler.getTagValue(stepDomNode, 
					Field.REF_TITLE_IN.name());
			
			refDate=XMLHandler.getTagValue(stepDomNode, 
					Field.REF_DATE_IN.name());
			
			refAuthor=XMLHandler.getTagValue(stepDomNode, 
					Field.REF_AUTHOR_IN.name());
			
			refPublisher=XMLHandler.getTagValue(stepDomNode, 
					Field.REF_PUBLISHER_IN.name());
			
			refAccessDate=XMLHandler.getTagValue(stepDomNode, 
					Field.REF_ACCESS_DATE_IN.name());
			
			serverResponseOutputField = XMLHandler.getTagValue(stepDomNode, 
					Field.SERVER_RESPONSE_OUT.name());
			
			successOuputField = XMLHandler.getTagValue(stepDomNode, 
					Field.SUCCES_OUT.name());
			
			createOnly = "Y".equals(XMLHandler.getTagValue(stepDomNode, 
					Field.CREATE_ONLY_IN.name()));
			
			categories = XMLHandler.getTagValue(stepDomNode, 
					Field.CATEGORIES_IN.name());
	
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getXML() throws KettleException {
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XMLHandler.addTagValue(Field.ENDPOINT_IN.name(), endPoint));
		xml.append(XMLHandler.addTagValue(Field.BOT_USER_IN.name(), botUser));
		xml.append(XMLHandler.addTagValue(Field.BOT_PASSWAORD_IN.name(), 
				botPassword));
		xml.append(XMLHandler.addTagValue(Field.SUMMARY_IN.name(), summary));
		
		xml.append(XMLHandler.addTagValue(Field.EPM_IN.name(), epm));
		xml.append(XMLHandler.addTagValue(Field.TEST_PHASE_IN.name(), 
				testPhase));

		xml.append(XMLHandler.addTagValue(Field.REF_URL_IN.name(), refUrl));
		xml.append(XMLHandler.addTagValue(Field.REF_TITLE_IN.name(), refTitle));
		xml.append(XMLHandler.addTagValue(Field.REF_DATE_IN.name(), refDate));
		xml.append(XMLHandler.addTagValue(Field.REF_AUTHOR_IN.name(), refAuthor));
		xml.append(XMLHandler.addTagValue(Field.REF_PUBLISHER_IN.name(), refPublisher));
		xml.append(XMLHandler.addTagValue(Field.REF_ACCESS_DATE_IN.name(), refAccessDate));
		
		xml.append(XMLHandler.addTagValue(Field.CATEGORIES_IN.name(), categories));
		
		xml.append(XMLHandler.addTagValue(Field.SERVER_RESPONSE_OUT.name(), serverResponseOutputField));
		xml.append(XMLHandler.addTagValue(Field.SUCCES_OUT.name(), successOuputField));
		
		xml.append(XMLHandler.addTagValue(Field.CREATE_ONLY_IN.name(), createOnly));

		return xml.toString();
	}

	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		//NOVOS
		endPoint = repository.getStepAttributeString(stepIdInRepository, 
						Field.ENDPOINT_IN.name());
				
		botUser = repository.getStepAttributeString(stepIdInRepository, 
						Field.BOT_USER_IN.name());
				
		botPassword = repository.getStepAttributeString(stepIdInRepository, 
						Field.BOT_PASSWAORD_IN.name());
				
		summary = repository.getStepAttributeString(stepIdInRepository, 
						Field.SUMMARY_IN.name());

		epm = repository.getStepAttributeString(stepIdInRepository, 
				Field.EPM_IN.name());
		
		testPhase = repository.getStepAttributeBoolean(stepIdInRepository, 
				Field.TEST_PHASE_IN.name());

		refUrl = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_URL_IN.name());
		refTitle = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_TITLE_IN.name());
		refAuthor = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_AUTHOR_IN.name());
		refDate = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_DATE_IN.name());
		refPublisher = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_PUBLISHER_IN.name());
		refAccessDate = repository.getStepAttributeString(stepIdInRepository, 
				Field.REF_ACCESS_DATE_IN.name());
		
		categories = repository.getStepAttributeString(stepIdInRepository, 
				Field.CATEGORIES_IN.name());
		
		serverResponseOutputField = repository.getStepAttributeString(stepIdInRepository, 
				Field.SERVER_RESPONSE_OUT.name());
		
		successOuputField = repository.getStepAttributeString(stepIdInRepository, 
				Field.SUCCES_OUT.name());
		
		createOnly = repository.getStepAttributeBoolean(stepIdInRepository, 
				Field.CREATE_ONLY_IN.name());

	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {


		//NOVOS
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
						Field.ENDPOINT_IN.name(), endPoint);
				
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
						Field.BOT_USER_IN.name(), botUser);
				
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
						Field.BOT_PASSWAORD_IN.name(), botPassword);
				
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
						Field.SUMMARY_IN.name(), summary);		
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.EPM_IN.name(), epm);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.TEST_PHASE_IN.name(), testPhase);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_URL_IN.name(), refUrl);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_TITLE_IN.name(), refTitle);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_AUTHOR_IN.name(), refAuthor);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_PUBLISHER_IN.name(), refPublisher);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_DATE_IN.name(), refDate);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.REF_ACCESS_DATE_IN.name(), refAccessDate);
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.CATEGORIES_IN.name(), categories);
		
			
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SERVER_RESPONSE_OUT.name(), serverResponseOutputField);
		repository.saveStepAttribute(idOfTransformation, idOfStep, 
				Field.SUCCES_OUT.name(), successOuputField);

		repository.saveStepAttribute(idOfTransformation, idOfStep, 
		Field.CREATE_ONLY_IN.name(), createOnly);
		
	}

	public void setDefault() {
		
		endPoint="https://test.wikipedia.org/w/api.php";
				//Credenciais
		botUser="BOTUSERNAME";
		botPassword="BOTPASSWORD";
		summary="Criação do artigo. Realizada pelo bot"; 
								//"created with basic information - (by bot)";
		//Fase de teste do bot?
		testPhase=true;
		//Para fase: até 3 edições por minuto
		epm = "3";
		
		refUrl = "";
		refAuthor="";
		refTitle="";
		refDate="dddd-mm-dd";
		refPublisher="";
		refAccessDate="";
		
		categories = "";
		
		serverResponseOutputField = "serverResponse";
		successOuputField = "successOnEdit";
		
		createOnly = true;
		
	}

	// Para os campos Field.OUT_*, refere-se ao tipo dos campos cujos nomes sao
	// especificados pelas estas variaveis desta classe Meta
	public int getFieldType(Field field) {
		
		if (field == Field.SERVER_RESPONSE_OUT)
			return ValueMetaInterface.TYPE_STRING;
		
		if (field == Field.SUCCES_OUT)
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
	
		if (field == Field.SERVER_RESPONSE_OUT)
			return new ValueMetaString(name);
		
		if (field == Field.SUCCES_OUT)
			return new ValueMetaBoolean(name);
		
		return new ValueMetaString(name);
	}

	/**
	 * it describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		
		
		//Remover campo de entrada na saida
		//inputRowMeta.clear();
		/*
		try {
			
			inputRowMeta.removeValueMeta("articleContent");
			
			
		} catch (KettleValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		ValueMetaInterface field = null;

		field = getValueMeta(serverResponseOutputField, Field.SERVER_RESPONSE_OUT);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = getValueMeta(successOuputField, Field.SUCCES_OUT);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
	}

	// Getters & Setters

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getBotUser() {
		return botUser;
	}

	public void setBotUser(String botUser) {
		this.botUser = botUser;
	}

	public String getBotPassword() {
		return botPassword;
	}

	public void setBotPassword(String botPassword) {
		this.botPassword = botPassword;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getSuccessOuputField() {
		return successOuputField;
	}

	public void setSuccessOuputField(String successOuputField) {
		this.successOuputField = successOuputField;
	}

	public String getServerResponseOutputField() {
		return serverResponseOutputField;
	}

	public void setServerResponseOutputField(String serverResponseOutputField) {
		this.serverResponseOutputField = serverResponseOutputField;
	}

	public String getEpm() {
		return epm;
	}

	public void setEpm(String epm) {
		this.epm = epm;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public Boolean isCreateOnly() {
		return createOnly;
	}

	public void setCreateOnly(Boolean createOnlyIn) {
		this.createOnly = createOnlyIn;
	}

	public String getRefUrl() {
		return refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}

	public String getRefTitle() {
		return refTitle;
	}

	public void setRefTitle(String refTitle) {
		this.refTitle = refTitle;
	}

	public String getRefAuthor() {
		return refAuthor;
	}

	public void setRefAuthor(String refauthor) {
		this.refAuthor = refauthor;
	}

	public String getRefPublisher() {
		return refPublisher;
	}

	public void setRefPublisher(String refPublisher) {
		this.refPublisher = refPublisher;
	}

	public String getRefDate() {
		return refDate;
	}

	public void setRefDate(String refDate) {
		this.refDate = refDate;
	}

	public String getRefAccessDate() {
		return refAccessDate;
	}

	public void setRefAccessDate(String refAccessDate) {
		this.refAccessDate = refAccessDate;
	}

	public Boolean isTestPhase() {
		return testPhase;
	}

	public void setTestPhase(Boolean testPhase) {
		this.testPhase = testPhase;
	}
}
