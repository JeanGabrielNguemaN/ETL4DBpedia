package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import br.ufrj.ppgi.greco.kettle.dbpedia.dao.TemplateManager;
import br.ufrj.ppgi.greco.kettle.dbpedia.entity.Template;
import br.ufrj.ppgi.greco.kettle.dbpedia.wikipedia.template.TemplateExtractor;

public class TemplatesLoaderStep extends BaseStep implements StepInterface {

	public TemplatesLoaderStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		TemplatesLoaderStepMeta meta = (TemplatesLoaderStepMeta) smi;
		TemplatesLoaderStepData data = (TemplatesLoaderStepData) sdi;

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
			
			//obter o padrão
			String templatePattern=meta.getTemplatePattern();
			
			//obter os templates de infobox iniciando com padao (default Info/)
			data.infoboxTemplates = getInfoboxTemplates(templatePattern);
			
			data.populatedTemplates= new HashSet<String>();
		}
		
		//Configurar, processar e gerar as rows de saída
		generateOutputRows(data);
		
		//Apenas uma row de entrada gera todas as rows de saída
		return true;
	}

	private void generateOutputRows(TemplatesLoaderStepData data) throws KettleStepException {
		
		//Templates
		List<Template> templates = data.infoboxTemplates;
		
		//Casa não haja templates, não há rowa na saída
		if(templates==null || templates.size()==0) {
			return ;
		}
		
		//Iterar para gerar um row para cada template.
		for (Template template: templates) {
		
			int outputRowPosition = 0;
			
			//Output são duas colunas
			Object[] outputRow = new Object[1];
			
			//popular template
			
			String templateName=template.getTitle();  //template.getTitle().split(":")[1];
			
			if(!data.populatedTemplates.contains(templateName)) {
				
				popularTemplate(template);
				
				data.populatedTemplates.add(templateName);
				
				//gerar um valor para a primeira coluna (nome de template)
				outputRow = RowDataUtil.addValueData(outputRow, outputRowPosition++, templateName);
				// Coloca linha no fluxo
			    putRow(data.outputRowMeta, outputRow);
			}
		}
	}
	
	private int popularTemplate(Template template) {
		
		TemplateManager  templateManager= new TemplateManager();
		
		return templateManager.instert(template);
	}

	/**
	 * Obter os templates de infobox
	 * @return Lista de template de infobox
	 */
	private List<Template>  getInfoboxTemplates(String pattern) {
		
		//Objeto que encapsula a  extração
		TemplateExtractor extractor= new TemplateExtractor();
	
		//Obter todos os templates que começam "/Info", sem exceção
		ArrayList<Template> templates= extractor.getAllTemplates(pattern);
		
		//Dentro dos templates, recuerar apenas apenas que sao de Infobox
		templates= extractor.getInfoboxTemplates(templates);
		
		return templates;
	}
}
