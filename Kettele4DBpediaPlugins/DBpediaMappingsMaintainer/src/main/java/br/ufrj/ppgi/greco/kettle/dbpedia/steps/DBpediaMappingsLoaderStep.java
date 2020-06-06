package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.HashMap;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
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
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateMappingManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.TemplateMapping;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Declaration;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.interpreter.Utilities;
import br.ufrj.ppgi.greco.kettle.dbpedia.mappings.parser.MappingParser;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;

public class DBpediaMappingsLoaderStep extends BaseStep implements StepInterface {

	public DBpediaMappingsLoaderStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		DBpediaMappingsLoaderStepMeta meta = (DBpediaMappingsLoaderStepMeta) smi;
		DBpediaMappingsLoaderStepData data = (DBpediaMappingsLoaderStepData) sdi;

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
			
			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, 
						getStepname(), null, null, this);
			
			//obter indexes dos fields
			data.fieldIndexHashMap=getFieldIndexHashMap(data.outputRowMeta);
			
			String field="MappingsStatus";
			data.indexOfMappingsStauts = data.fieldIndexHashMap.get(field);
		}
		
		//Configurar, processar e gerar as rows de saída
		
		String field="Template";
		
		String templateName=getFieldValue(row, field);
		
		generateOutputRows(row, data, templateName);
		
		//evitar sobrecarga do servidor
		//intervalo de tempo entre processamento de rows
        try {
        	
			java.util.concurrent.TimeUnit.SECONDS.sleep(2);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			
		}
		return true;
		
	}

	private void generateOutputRows(Object[] row, DBpediaMappingsLoaderStepData data, String templateName) throws KettleStepException {
		
		String mappingsStatus="Mappings inexistentes";
		
		boolean populated=popularMappingsDoTemplate(templateName);
			
		if(populated) {
			mappingsStatus="Mappings persistidos";
		}
		
		//ouput
		Object[] outputRow = row;
		
		//gerar 
		outputRow = RowDataUtil.addValueData(outputRow, 
				data.indexOfMappingsStauts, mappingsStatus);
			
		// Coloca linha no fluxo de saida
		putRow(data.outputRowMeta, outputRow);
	}
	
	/**
	 * Popular os mapeamentos de templates na base local
	 * @param templateName. Exemplo: String templateName = "Info/Filme";
	 * @return
	 */
	private boolean popularMappingsDoTemplate(String templateName) {

		//obter um objeto paser, necessário para gerar os mapeamentos
		MappingParser mparser= MappingParser.getInstance();
		
		//obter os mapeamentos do template
		String mappings =
				Utilities.extractDMLMappings(templateName);//obterConteudo("./files/mappingsDML3.txt");
		
		if(mappings==null ) {
			return false;
		}
		//Gerar os mapeamentos na forma de declarações
		Declaration declaration = mparser.gerarMapeamentos(templateName, mappings);
		
		if(declaration==null  ) {
			return false;
		}
	    return populateTemplateMapping(declaration, templateName, mappings);			
	}
	
	/**
	 * @param declaration
	 * @param templateName 
	 * @param mappings 
	 */
	private boolean populateTemplateMapping(Declaration declaration, String templateName, String mappings) {
		
		if(declaration==null){
			System.out.println("Declaraçao nula");
			return false;
		}
		
		//template geração
		TemplateManager templateManager= new TemplateManager();
		
		templateManager.getTemplate(templateName);
		
		Template template = templateManager.getTemplate(templateName);
		
		if(template==null){
			return false;
		}
		
		//criar a estrutura dos mapeamentos de um templates
		TemplateMapping templateMapping = 
				(TemplateMapping)declaration.interpret(template);
	
		if(templateMapping==null){
			return false;
		}
			
		templateMapping.setMappings(mappings);
			
		//popuplar. Vericar sempre o database usado. Para testes: dbpediaexptest.
		new TemplateMappingManager().insertTemplateMapping(templateMapping);
		
		return true;
	}
	
	/**
	 * extrair valor do field(campo) no row (registro).
	 * @param row
	 * @param field
	 * @return String. Retorna valor do field no row corrente.
	 * @throws KettleValueException
	 */
	private String getFieldValue(Object[] row, String field) throws KettleValueException {
		
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		int index=rowMetaInterface.indexOfValue(field);
		
		if(index==-1) {
			return null;
		}
		//Obter o tipo do campo
		ValueMetaInterface valueMetaData=rowMetaInterface.getValueMeta(index);
		
		int tipo = valueMetaData.getType();
		
		String valorField="";
		
		if (tipo==ValueMetaInterface.TYPE_STRING) {
			//Valor da propriedade
			valorField = rowMetaInterface.getString(row,index);
		} 
		return valorField;
	}
	
	/**
	 * Obter field de entrada com seus indexes dentro do row
	 * @param rowMetaInterface
	 * @return
	 */
	private HashMap<String, Integer> getFieldIndexHashMap(RowMetaInterface rowMetaInterface) {
		
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
	
}
