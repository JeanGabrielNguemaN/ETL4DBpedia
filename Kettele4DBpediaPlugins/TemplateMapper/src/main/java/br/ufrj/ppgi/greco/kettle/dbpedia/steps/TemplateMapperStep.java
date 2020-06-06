package br.ufrj.ppgi.greco.kettle.dbpedia.steps;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.TemporyDataHandler;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;

public class TemplateMapperStep
 extends BaseStep implements StepInterface {

	//private StringBuffer mappings;

	public TemplateMapperStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			
			return true;
		} else
			return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}

	/**
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		TemplateMapperStepMeta meta = (TemplateMapperStepMeta) smi;
		TemplateMapperStepData data = (TemplateMapperStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
		Object[] row = getRow();
		
		if (row == null) { // N�o h� mais linhas de dados
			setOutputDone();
			return false;
		}

		if (first) { // Executa apenas uma vez. Variavel first definida na
						// superclasse
			first = false;

			// Obtem todas as colunas até o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = rowMeta.clone();
			
			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			//guardar os fields com seus indexes (DEVE GUARDAR DEPOIS DESTE METODO)
			data.fieldsIndexesHashMap=obterFieldIndexOf(data.outputRowMeta);
			
			data.templateFieldIndex=data.fieldsIndexesHashMap.get("template");
			
			//obter todos os mapeamentos "Campo de dominio --> propriedade"
			data.mappings=obterMapeamentos(meta, data, row);
			
			data.mappingsField= "templateMappings";
		
		}
		 
		//Extrair o valor do template do row atual para filtrar pelo selecionado
		RowMetaInterface rowMetaInterface = getInputRowMeta();
		
		//Obter o tipo do campo
		ValueMetaInterface valueMetaData=rowMetaInterface.getValueMeta(data.templateFieldIndex);
		
		String valorField = rowMetaInterface.getString(row,data.templateFieldIndex);

		if(valorField!=null && valorField.trim().contentEquals(meta.getSelectedTemplale().trim()+" %")) {
			
			//gravar em um campo de saída 
			// todos os mapeamentos  "Campo de dominio --> propriedade"
			putOutRowByField(row, meta, data);
		}
		
	
		//proxima row
		return true;
	}

    /**
	 * Grava todos os mapeiamento em um campo
	 * @param inputRow
	 * @param meta
	 * @param data
	 * @param stringValue
	 * @throws KettleStepException
	 */
	private void putOutRowByField(Object[] inputRow, TemplateMapperStepMeta meta, TemplateMapperStepData data) throws KettleStepException {
		
		//manter os dados de entrada
		Object[] outputRow =inputRow;
		
		//index do field
		int index=data.fieldsIndexesHashMap.get(data.mappingsField);
		
		//guardar
		outputRow = RowDataUtil.addValueData(outputRow, index, data.mappings);
		
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
	 * Obter todos mapeamentos em uma string
	 * @param meta
	 * @param data
	 * @param row
	 * @throws KettleException
	 */
	private String obterMapeamentos(TemplateMapperStepMeta meta, TemplateMapperStepData data, Object[] row) throws KettleException {
		// Add data properties
		DataTable<String> table = meta.getMapTable();
		
		long tableSize= table.size();
		
		//não há mapeamentos
		if(tableSize==0) {
			return null;
		}
		
		String templatename=meta.getSelectedTemplale().split("-")[0].trim();
		
		StringBuffer mappings = 
				new StringBuffer("<infoboxTemplate name=\""
					+templatename+"\">\n\t<maps>\n");
		
		//campos de dominios 
		Set<String> camposDoDominioMapeados= 
				new HashSet<String>();
		
		//para propriedades do templates
		String[] propriedadesDoTemplateArr=
				new String[1000];
		//para guardar as propriedades de templates
		int j=0; 
		
		//iterar nos mapeamentos
		for (int i = 0; i < table.size(); i++) {
			//obter o campo de dominio atual
			String campoDoDominio = 
					table.getValue(i,TemplateMapperStepMeta.Field.
							MAP_TABLE_DOMAIN_FIELD_FIELD_NAME.name());
			
			//Evitar repetições de campos de dominio: faz nada se repetiu o campo
			if(camposDoDominioMapeados.contains(campoDoDominio)) {
				continue;
			}
			//AQUI: o campo de dominio não está ainda no conjunto
			
			//propriedade mapeada
			String templatePropertyField = 
					table.getValue(i, 
						TemplateMapperStepMeta.Field.MAP_TABLE_TEMPLATE_PROPERTY_FIELD_NAME.name());
			
			//propriedade do template
			String propriedadeDoTemplate = getInputRowMeta().getString(row, templatePropertyField, templatePropertyField);

			//não inserir campo ou propriedade vazio
			if ("".equals(propriedadeDoTemplate) || "".equals(campoDoDominio)) {
				continue;
			}
			
			//criar o mapeamento e armazenar no buffer
			String mapeamento= "\t\t<map>\n\t\t\t<from>"+campoDoDominio+"</from>\n\t\t\t<to>"+propriedadeDoTemplate+"</to>\n\t\t</map>\n";
					
			mappings.append(mapeamento);
					
			//adicionar o campo no conjunto, só quando tiver mapeamento
			camposDoDominioMapeados.add(campoDoDominio);
			
			propriedadesDoTemplateArr[j]=propriedadeDoTemplate;
			j++;
		}// for
		
	    //Savar os campos mapeados	
	    mappings.append("\t</maps>\n</infoboxTemplate>\n");
	    
	    //Salvar os mapeamentos
	    data.mappings=mappings.toString();
	    
	    //Salvar propriedades mapeadas
	    data.mappedTemplateProperties=obterPropriedadesMapeadas(templatename,propriedadesDoTemplateArr, j);;
	    
		return mappings.toString();
	}

	/***
	 * Guardar propriedades de template em um repositorio temporário
	 * @param propriedadesDoTemplateArr
	 * @param numberOfMappings
	 * @param templatename 
	 */
	private String[] obterPropriedadesMapeadas(String templatename, String[] propriedadesDoTemplateArr, int numberOfMappings) {
		
		if(numberOfMappings==0) {
			return null;
		}
		
		//Primeira linha contem o template, as outras as propriedades
		StringBuffer buffer= new StringBuffer("templatename="+templatename+"\n");
		
		//propriedades
		String[] propriedades = new String[numberOfMappings];
		int k=0;
		
		Set<String> propriedadesDoTemplateSet=new HashSet<String>();
		
		
		for (int i=0; i<numberOfMappings;i++) {
			
			String propriedade=propriedadesDoTemplateArr[i];
			
			//propriedade duplicada
			if(propriedadesDoTemplateSet.contains(propriedade)) {
				continue;
			}
			
			propriedades[k]=propriedade;
			k++;
			
			propriedadesDoTemplateSet.add(propriedade);
			
			buffer.append(propriedade+"\n");
		}
		
		TemporyDataHandler.store("mappedproperties", buffer.toString());
		
		return propriedades;
	}

}