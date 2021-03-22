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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.jdom2.Element;
import org.jdom2.Document;
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

import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.val.TemplatePropertyValueValidator;
import br.ufrj.ppgi.greco.kettle.dbpedia.val.ValidationResult;
import net.sf.saxon.functions.Collection;


public class ArticleContentStep extends BaseStep implements StepInterface {

	public ArticleContentStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		ArticleContentBuilderStepMeta meta = (ArticleContentBuilderStepMeta) smi;
		ArticleContentStepData data = (ArticleContentStepData) sdi;

		// Obtem a próxima linha do fluxo de entrada e 
		// termina caso nao haja mais entrada
		Object[] row = getRow();

		//termina caso nao haja mais entrada
		if (row == null) { // N�o h� mais linhas de dados
			setOutputDone();
			return false;
		}

		// Executa apenas uma vez.Variavel first definida na // superclasse
		if (first) {  
			first = false;

			// Obtem todas os fields de entrada: eles vêm do step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			
			data.outputRowMeta = rowMeta.clone();
			
			data.fieldsIndexesHashMap=obterFieldIndexOf(data.outputRowMeta);
			
			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			//armazenar os fields com seus indexes
			data.fieldsIndexesHashMap=obterFieldIndexOf(data.outputRowMeta);
			
			//Obter os mapeamentos
			data.mappings=obterMappings(row);
			
			//Obter nome do infobox
			data.infoboxName=getInfoboxName(data.mappings);
					
			//armazenar os dados de entrada do dialog
			data.titleField= meta.getTitleField();
			
			//campo que servirá para introdução do artigo
			data.firstSectionField=meta.getFirstSectionField();
		}
		
		//gerar conteúdo
		try {
			
			boolean thisPageExist="Y".contentEquals(getFieldContent(row, "articleExists"));
			
			//se a página existir não inserir
			if(thisPageExist) {
				return true;
			}
			
			//Campos(colunas) mantidos no row da saída.
			String[] fields = new String[] {"articleExists","articleTitle", "potentialArticleTitle"};
			
			//Obter indexes dos campos(colunas) que devem ser removidos no row da saida
			int[] indexes= getRemovedIndexesInOutput(fields);
			
			
			if(indexes==null) {
				return true;
			}
			
			//Como a entrada foi limpada, precisamos de novos registros
			//Object[] outputRow = new Object[3];
			Object[] outputRow = RowDataUtil.removeItems(row, indexes);
			
			//OUTPUT 1
			//extrair título e introduçao
			//String titleFieldContent = getFieldContent(row, data.titleField);
			
			//Último indice dos campos do row
			int index=fields.length-1;
			
			//Atualizar registro com título
			//outputRow = RowDataUtil.addValueData(outputRow, index + 1 , titleFieldContent);
			//outputRow = RowDataUtil.addValueData(outputRow, index , titleFieldContent);
			
			//logBasic("==== TitleFieldContent :"+titleFieldContent);
			
			//outputRow = RowDataUtil.addValueData(outputRow, index + 1 , titleFieldContent);
			
			//OUTPUT 2
			//forma de geração do conteúdo
			String format="json";
					
			//Gerar o conteudo do infobox no formato Json
			String infoboxContent = gerarConteudoDoInfobox(meta, data, row, format);
			
			
			
			outputRow = RowDataUtil.addValueData(outputRow, index + 1 , infoboxContent);
			
			
			//OUTPUT 3
			String firstSectionFieldContent = getFieldContent(row, data.firstSectionField);
			
			
			
			//Atualizar registro com infobox
			//outputRow = RowDataUtil.addValueData(outputRow, index + 3 , firstSectionFieldContent);
			//outputRow = RowDataUtil.addValueData(outputRow, index , firstSectionFieldContent);
			outputRow = RowDataUtil.addValueData(outputRow, index + 2 , firstSectionFieldContent);
			
			//acrescentar um campo na saída com conteúdo extraído

			 putRow(data.outputRowMeta, outputRow);
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		

		return true;
	}
	
	/**
	 * Obter os indixes na row de entrada dos campos não listados
	 * @param preservedFields
	 * @return
	 */
	private int[] getRemovedIndexesInOutput(String[] preservedFields) {
	
		if (preservedFields==null) {
			return null;
		}
		
		int[] indexes=null; //ex: {0,1,2,3,4,5,6,7,8,9,10,11};
		
		
		//para obter index do campo
				
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		String[] fieldnames = rowMetaInterface.getFieldNames();
		
		//novo array
		indexes= new int[fieldnames.length- preservedFields.length];
		

		List<String> preservedFieldsList = Arrays.asList(preservedFields);
		
		int i=0;
		//iterar para recurar os indexes
		for(String field : fieldnames) {
			//o o field não estiver nos campos preservados
			if( ! preservedFieldsList.contains(field)) {
				//recuperar index do field
				int index=rowMetaInterface.indexOfValue(field);
				
				indexes[i]=index;
				
				i++;
			}
			
		}	
		
		return indexes;
	}

	/**
	 * Obter mapeamento a partir da entrada
	 * @param row
	 * @return
	 * @throws KettleException
	 */
    private String obterMappings(Object[] row) throws KettleException {
		
    	//O campo de entrada templateMappings armazenado os mapeamentos
    	String field = "templateMappings";
		
    	String mappings = extrairValorDoFieldNoRow(row, field);
		
		return mappings;
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
	 * Obter field de entrada com seus indexes dentro do row
	 * @param rowMetaInterface
	 * @return
	 */
	private HashMap<String, Integer> obterFieldIndexOf(RowMetaInterface rowMetaInterface) {
		 
		if(rowMetaInterface==null) {
			return null;
		}
		
		String[] fields=rowMetaInterface.getFieldNames();
		 
		if(fields==null) {
			return null;
		}
		
		HashMap<String, Integer> fieldIndexHashMap=new HashMap<String, Integer>();
		 
		//armazenar os fields com seus indices
	    for (String field: fields) {
			 
			 int index=rowMetaInterface.indexOfValue(field);
			 
			 fieldIndexHashMap.put(field, index);
		 }
		 
		return fieldIndexHashMap;
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
	private String gerarConteudoDoInfobox(ArticleContentBuilderStepMeta meta, ArticleContentStepData data, Object[] row, String format) throws KettleException, JDOMException, IOException {
		 
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
	
	private String getInfoboxName(String mappings) {
		
		if(mappings==null) {
			return null;
		}
		
		String info = mappings.split("\"")[1];
		
		return info;
	}

	/*
	/**
	 * Extrair introdução do artigo
	 * @param row
	 * @param introductionOfArticle
	 * @return
	 * @throws KettleValueException 
	 
	private String getFirstFieldContent(Object[] row, String introductionOfArticle) throws KettleValueException {

		return extrairValorDoFieldNoRow(row, introductionOfArticle);
	}
	 */
	/**
	 * Extrair título do artigo
	 * @param row
	 * @param field
	 * @return
	 * @throws KettleValueException 
	 */
	private String getFieldContent(Object[] row, String field) throws KettleValueException {

		return extrairValorDoFieldNoRow(row, field);
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

	/**
	 * Gerar o par propriedade de infobox e seu valor 
	 * @param data
	 * @param row
	 * @param field
	 * @param propriedadeInfobox
	 * @return
	 * @throws KettleValueException
	 */
	private String gerarParPropriedadeInfoboxEValor(ArticleContentStepData data, Object[] row, String field,
			String propriedadeInfobox) throws KettleValueException {
		
		String parPropriedadeValor=null;
		
		//extrair valor do field(campo) no row (registro)
		String valorCampo = extrairValorDoFieldNoRow(row, field);//extrairValorFieldV2(row, field); 
		
		//====================== VALIDAÇÃO  ========================================
		
		//Para a validação do valor da propriedade
		TemplatePropertyValueValidator propertyValueValidator 
					= new TemplatePropertyValueValidator();
				
		//Obter id do template atual
		int idtemplate	 = new TemplateManager()
						.getTemplate(data.infoboxName).getId();
			
		//validar 
		ValidationResult validationResult = propertyValueValidator
				.validate(idtemplate, propriedadeInfobox , valorCampo);
		
		//valor válido?
		if(validationResult.isValid()) {
			//sendo válido adiciona
			parPropriedadeValor=
					"{"
					    + "\"property\":"+"\""+propriedadeInfobox+"\""+","
						+ "\"value\":"   +"\""+validationResult.getValue()+"\""
					 +"}";
			
		} else {
			//não inserir, pois o valor inválido
			parPropriedadeValor=
					"{"
					    + "\"property\":"+"\""+propriedadeInfobox+"\""+","
						+ "\"value\":"   +"\""+""+"\""
					 +"}";
		}
	
		//======================================================
	
		return parPropriedadeValor;
	}

	/**
	 * extrair valor do field(campo) no row (registro).
	 * @param row
	 * @param field
	 * @return String. Retorna valor do field no row corrente.
	 * @throws KettleValueException
	 */
	private String extrairValorFieldV2(Object[] row, String field) throws KettleValueException {
		
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		int index=rowMetaInterface.indexOfValue(field);
		
		//Obter o tipo do campo
		ValueMetaInterface valueMetaData=rowMetaInterface.getValueMeta(index);
		
		//ValueMetaData não existe
		if(valueMetaData==null) {
			return null;
		}
		
		int tipo = valueMetaData.getType();
		
		String valorField="";
		
		if (tipo==ValueMetaInterface.TYPE_STRING) {
			//Valor da propriedade
			valorField = rowMetaInterface.getString(row, index);
			
			 
			
		} else if (tipo==ValueMetaInterface.TYPE_NUMBER) {

			//obter o valor
			valorField += rowMetaInterface.getNumber(row,index);
		}
		return valorField;
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
}
