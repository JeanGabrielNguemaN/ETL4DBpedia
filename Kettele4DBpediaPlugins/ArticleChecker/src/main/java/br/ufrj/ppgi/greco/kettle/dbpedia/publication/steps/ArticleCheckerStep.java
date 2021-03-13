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

package br.ufrj.ppgi.greco.kettle.dbpedia.publication.steps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
//import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.CheckResult;
import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.Entity;
import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.EntityManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.publication.wikipedia.WikipediaPageChecker;
import br.ufrj.ppgi.greco.kettle.dbpedia.utils.TemplatesHandler;

/**
* This class is responsible for the logic processing in the Step, and according to it generating rows 
* to its output when the transformation runs.  
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class ArticleCheckerStep extends BaseStep implements StepInterface {

	public ArticleCheckerStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		ArticleCheckerStepMeta meta = (ArticleCheckerStepMeta) smi;
		ArticleCheckerStepData data = (ArticleCheckerStepData) sdi;

		Object[] row = getRow();

		if (row == null) {
			
			setOutputDone();
			
			return false;
		}

		if (first) { // Executa apenas uma vez, first eh definido na superclasse
			first = false;
			
			data.selectedMappedProperties=meta.getSelectedMappedProperties().split(",");
				
			RowMetaInterface rowMeta = getInputRowMeta();
			
			data.inputRowSize = rowMeta.size();

			data.outputRowMeta = rowMeta.clone();
			
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			//guardar os fields com seus indexes
			data.fieldsIndexesHashMap=obterFieldIndexOf(data.outputRowMeta);
			
			//Obter os mapeamentos
			data.mappings=obterMappings(row);
			
			//Obter nome do infobox
			data.infoboxName=getInfoboxName(data.mappings);
			
			data.proposedTitleField = meta.getSelectedFieldForTitle();
		   
			data.proposedInfoboxTitleProperty = meta.getSelectedPropertyForInfoboxTitle();
			
			data.infoxTitleUsedInComparision=meta.getTitleUsedInInfoboxComparison();
			
			data.entityManager=new EntityManager();
			
		} //first
		//PROCESSAMENTO DENTRO DO ROW
		//obter o título
		String proposedTitle = extrairValorField(row,  data.proposedTitleField);
		
		String proposedInfoboxTitle = extrairValorField(row,  data.proposedInfoboxTitleProperty);
		
		String format="json";
		
		try {
			//Gerar o conteudo do infobox no formato Json
			String infoboxContentJSON = gerarConteudoDoInfobox(meta, data, row, format);
			
			//Não havendo conteúdo, não insere na saída
			if(  infoboxContentJSON==null) {
				return true;
			}
			
			//Gerar a entidade que serivrá para comparaçao
			Entity entity=data.entityManager.popularEntity(proposedTitle, proposedInfoboxTitle, infoboxContentJSON, 
					data.selectedMappedProperties);
		
			String[] mappedTemplateProperties= 
					TemplatesHandler.getTemplatesProperties(data.infoboxName);
					
			//Instanciar a checker que verifica se o artigo já existe/Infobox na Wikipedia
			WikipediaPageChecker checker=new WikipediaPageChecker();
		
			//propriedades do templates
			checker.setTemplateProperties(mappedTemplateProperties);
			
			//Realizar a pesquisa do artigo 
			//representado pela entidade e aramazenar o resultado
			CheckResult checkResult = checker.checarEntidadePorInfobox(entity, data.infoboxName);   
			 
			//armazenar campo de existêcnia de artigo 
			Boolean thisArticleExists = checkResult.isPageFound();
			
			//por default o artigo não existe
			String pageTitle =null;
			//artigo existe então, obter seu título
			if(checkResult.isPageFound()) {
				pageTitle= checkResult.getPage().getTitle();
			}
			
			putOutFields(row, meta, data, pageTitle, thisArticleExists, proposedTitle);
			
			//evitar sobrecarga do servidor
	         java.util.concurrent.TimeUnit.SECONDS.sleep(5);
		
		} catch (JDOMException | InterruptedException | IOException e) {
			logBasic("Exeception: "+e.getMessage());
		}
		
		return true;
	}
	
	private void putOutFields(Object[] inputRow, ArticleCheckerStepMeta meta, ArticleCheckerStepData data, 
			String articleTitleValue, Boolean thisInfoboxExistsValue, String potentialArticleTitleValue) throws KettleException {
		//manter os dados de entrada
		Object[] outputRow =inputRow;
		
		//Incluir o titulo
		//index do field
		int index=data.fieldsIndexesHashMap.get("articleTitle");
		outputRow = RowDataUtil.addValueData(outputRow, index, articleTitleValue);
	
		//Incluir campo se página existe
		index=data.fieldsIndexesHashMap.get("articleExists");
		outputRow = RowDataUtil.addValueData(outputRow, index, thisInfoboxExistsValue);
		 
		//Incluir titulo potencial na saída
		 index=data.fieldsIndexesHashMap.get("potentialArticleTitle");
		 outputRow = RowDataUtil.addValueData(outputRow, index, potentialArticleTitleValue);				
		// Coloca linha no fluxo
	     putRow(data.outputRowMeta, outputRow);
	}

    /**
	 * Obter field de entrada com seus indexes dentro do row
	 * @param rowMetaInterface
	 * @return
	 */
	private HashMap<String, Integer> obterFieldIndexOf(RowMetaInterface rowMetaInterface) {
		
		HashMap<String, Integer> fieldIndexHashMap= null;
		 
	    //objeto do RowMetaInterface é singleton. Ou seja, mudanças refletem imediatmente na saida.
		//RowMetaInterface rowMetaInterface = data.outputRowMeta;
		
		if(rowMetaInterface==null) {
			return null;
		}
		
		String[] fields=rowMetaInterface.getFieldNames();
		 
		if(fields==null) {
			return null;
		}
		
		
		fieldIndexHashMap=new HashMap<String, Integer>();
		
	    for (String field: fields) {
			 
			 int index=rowMetaInterface.indexOfValue(field);
			 
			 fieldIndexHashMap.put(field, index);
		 }
		 
		return fieldIndexHashMap;
		
	}
	
	/**
	 * extrair valor do field(campo) no row (registro).
	 * @param row
	 * @param field
	 * @return String. Retorna valor do field no row corrente.
	 * @throws KettleValueException
	 */
	private String extrairValorField(Object[] row, String field) throws KettleValueException {
		
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		int index=rowMetaInterface.indexOfValue(field);
		
		//Obter o tipo do campo
		ValueMetaInterface valueMetaData=rowMetaInterface.getValueMeta(index);
			
		if(valueMetaData==null) {
			return null;
		}
		
		int tipo = valueMetaData.getType();
		
		String valorField="";
		
		if (tipo==ValueMetaInterface.TYPE_STRING) {
			//Valor da propriedade
			valorField = rowMetaInterface.getString(row,index);
			
	
		} /*else if (tipo==ValueMetaInterface.TYPE_NUMBER) {

			//obter o valor
			valorPropriedade += rowMetaInterface.getNumber(row,index);

		}*/
		return valorField;
	}
	
	/**
	 * Gerar conteudo do artigo
	 * @param meta
	 * @param data
	 * @param row
	 * @param format (ex: json) 
	 * @throws KettleException
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private String gerarConteudoDoInfobox(ArticleCheckerStepMeta meta, ArticleCheckerStepData data, Object[] row, String format) throws KettleException, JDOMException, IOException {
		 

		if((format==null) || 
				!(format.trim().toLowerCase().equals("json"))) {
			
			return null;
			
		}
		
		//Obter mapeamentos
		HashMap<String,String> campoToProperty= obterMapeamentosCampoPropriedade(data.mappings);
		
		Set<String> campos = campoToProperty.keySet();
	
		//OPTION JSON
		StringBuffer contexntJSON= new StringBuffer("");
	
		//primeiras linhas
		contexntJSON.append(
		  "{\"infobox\":" +"\n\t\t"
			    +"{\"nome\":"+"\""+data.infoboxName+"\""+","+"\n\t\t");
				
		contexntJSON.append("\"propertiesvalues\":[");
		
		//iterar nos mapeamentos
		int i=0;
		
		for (String campoDoDominio: campos) {
			
			String propriedadeDoTemplate= campoToProperty.get(campoDoDominio);
							
			//-------------------------------------------------------------------------	
			//obter o par propriedade valor em formato xml
			String propriedadeInfoboxValor = this
							.gerarParPropriedadeInfoboxEValor(data, row, campoDoDominio, 
									propriedadeDoTemplate);
					
			//armazenar no conteudo do infobox
			if(propriedadeInfoboxValor!=null) {

				contexntJSON.append(propriedadeInfoboxValor);
				
				if (i<campos.size()-1) {
					//json
					contexntJSON.append(",");
					
				}
			}
			
			i++;
				
		}// for
		
		contexntJSON.append("\n\t]"); //fecha o array propertiesvalues
		
		contexntJSON.append("}"); //fecha chave infobox
		
		//contexntJSON.append("}"); //fecha chave article
		
		contexntJSON.append("}"); //fecha chave root
		
		return contexntJSON.toString();
	}
	
	/**
	 * Obter mapeamentos (campo-->propriedade) na forma de HashMap
	 * @param mappings
	 * @return HashMap<campo de dominio, propriedade>
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private HashMap<String, String> obterMapeamentosCampoPropriedade(String mappings) throws JDOMException, IOException {
		
		if(mappings==null) {
			return null;
		}
		//jdom2 nao consegue ler string xml. O metodo build abaixo, so recebe arquivo ou url
		save("mappings.xml", mappings);
		
		File xmlSource = new File("mappings.xml");
		
		// read the XML into a JDOM2 document.
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(xmlSource);
        
        Element rootNode = jdomDocument.getRootElement();
        
        Element maps = rootNode.getChild("maps");
		
		List<Element> mapsList =  maps.getChildren("map");
		
		int maps_size=mapsList.size();
		
		//Não existindo mapeamento, retorna null
		if(maps_size==0) {
			
			return null;
		}
		
		HashMap<String, String> fieldToPropertyHashMap= new HashMap<String, String>();
		
 		for (int i = 0; i < maps_size; i++) {
			
		   Element map = (Element) mapsList.get(i);
		   
		   Element from=map.getChild("from");
		   String campoDeDominio = from.getText();
		  
		   Element to=map.getChild("to");
		   String  propriedade = to.getText();
		   
		   fieldToPropertyHashMap.put(campoDeDominio,propriedade);
		}
		return fieldToPropertyHashMap;
	}
	
	public void save(String repository, String content){
		
		try {
			BufferedWriter bwr = new BufferedWriter(
						new OutputStreamWriter(
							  new FileOutputStream(repository, 
								    false), StandardCharsets.UTF_8));
					
			//save contents of StringBuffer
			bwr.write(content.toString());
			
			//flush the stream
			bwr.flush();
			
			//close the stream
			bwr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Gerar Nome do infobox a partir dos  mapeamentos
	 * @param mappings
	 * @return
	 */
	private String getInfoboxName(String mappings) {
		
		if(mappings==null) {
			return null;
		}
		
		String info = mappings.split("\"")[1];
		
		return info;
	}
	
	
	/**
	 * Gerar o par propriedade de infobox e seu valor 
	 * @param data
	 * @param row
	 * @param field
	 * @param propriedadeInfobox
	 * @return
	 * @throws KettleValueException
	 */
	private String gerarParPropriedadeInfoboxEValor(ArticleCheckerStepData data, Object[] row, String field,
			String propriedadeInfobox) throws KettleValueException {
		
		String parPropriedadeValor=null;
		
		//extrair valor do field(campo) no row (registro)
		String valorCampo = extrairValorDoFieldNoRow(row, field);//extrairValorFieldV2(row, field); 
		
		parPropriedadeValor=
				"{"
				    + "\"property\":"+"\""+propriedadeInfobox+"\""+","
					+ "\"value\":"   +"\""+valorCampo+"\""
				 +"}";
			
		return parPropriedadeValor;
	}

	 /**
     * Extrair valor do field no row
     * @param row
     * @param field
     * @return String que é o valor
     * @throws KettleValueException
     */

	private String extrairValorDoFieldNoRow(Object[] row, String field) throws KettleValueException {
		//para obter index do campo
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		//index
		int index=rowMetaInterface.indexOfValue(field);
		
		//se o field não existe
		if(index==-1) {
			return null;
		}
		
		String fieldValue = rowMetaInterface.getString(row,index);
		
		return fieldValue;
	}
	
	/**
	 * Obter mapeamento a partir da entrada
	 * @param row
	 * @return
	 * @throws KettleException
	 */
	 private String obterMappings(Object[] row) throws KettleException {
			
	    String field = "templateMappings";
			
	    String mappings = extrairValorDoFieldNoRow(row, field);
			
		return mappings;
	}
}
