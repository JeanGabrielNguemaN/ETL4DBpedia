package br.ufrj.ppgi.greco.kettle.dbpedia.steps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.ufrj.ppgi.greco.kettle.dbpedia.templates.*;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.TemporyDataHandler;

public class TemplateSelectorStep
 extends BaseStep implements StepInterface {

	private static final Class<?> PKG = TemplateSelectorStepMeta.class; // for i18n purposes

	private StringBuffer fileContent;

	public TemplateSelectorStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			// TODO init something here if needed
			// ...
			return true;
		} else
			return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);

		// TODO finalize something here if needed
		// ...
	}

	/**
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		TemplateSelectorStepMeta meta = (TemplateSelectorStepMeta) smi;
		TemplateSelectorStepData data = (TemplateSelectorStepData) sdi;

		// Obtem linha do fluxo de entrada e termina 
		// caso nao haja mais entrada
		Object[] row = getRow();
		
		if (row == null) { // N�o h� mais linhas de dados
			setOutputDone();
			return false;
		}

		// Executa apenas uma vez. Variavel first definida na
		// superclasse
		if (first) { 
			
			first = false;

			// Obtem todas as colunas at� o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = rowMeta.clone();
		
			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			// Locate the row index for this step's field
		    // If less than 0, the field was not found.
		    data.templateFieldIndex = 
		    		data.outputRowMeta.indexOfValue( 
		    				meta.getTemplateFieldName() );
		    
		    
		    //Recuperar templates
		     String[] keyConcepts=meta.getKeyConcepts().split(",");
		      //Obter templates
		      
		     if(keyConcepts!=null ) {
		    	 
		    	 
		    	 TemplatesSearcher templateSelector = new TemplatesSearcher();
			     	
			     data.templates= templateSelector.selectTemplateCandidates(keyConcepts) ;
		     }
		     
		} //first
	
		//--------------------------------------------------------------------------------------------
		// Add data properties
		//DataTable<String> table = meta.getMapTable();
		//long tableSize= table.size();
				
		processarSaida(data, row);

		//tratar apenas um row, pois atende. Mais de um repete a mesma coisa
		return true;
	}

	private void processarSaida(TemplateSelectorStepData data, Object[] row) throws KettleStepException {
		
		if(data.templates==null){
			return ;
		}
	    //logBasic("==== data.templates.size ="+data.templates.size());	
	    Object[] outputRow = RowDataUtil.resizeArray(row, data.outputRowMeta.size() );
	    
	    List<TemplateCandidate> templatesCand = data.templates;
	    
	    //iterar
	    for (TemplateCandidate templateCand: templatesCand) {
	    	outputRow =RowDataUtil.allocateRowData( data.outputRowMeta.size());
	    		
	    	String templateValue=templateCand.getTitle().trim() 
    				+" - "+templateCand.getFrequence()+" %";
	   	  
	    	outputRow[data.templateFieldIndex] = templateValue;
	    	
	       	// put the row to the output row stream
	    	//this.logBasic("TEMPLATE VALUE = "+templateValue);
	    	
	       	putRow( data.outputRowMeta, outputRow );
	       	    
	    }
	    	
	    //Zerar para não duplificar os rows gerados.
	    saveRepository(data.templates);
	        
	    data.templates=null;
	    
	    // log progress if it is time to to so
	    if ( checkFeedback( getLinesRead() ) ) {
	    	logBasic( BaseMessages.getString( PKG, "DemoStep.Linenr", getLinesRead() ) ); // Some basic logging
	    }
	}

	/**
	 * Guardar os templates
	 * @param templates
	 */
	private void saveRepository(List<TemplateCandidate> templates) {
		
		List<TemplateCandidate> templatesCand = templates;
	    
		StringBuffer buffer= new StringBuffer("");
		 
	    for (TemplateCandidate templateCand: templatesCand) {
	    	
	    	String templateValue=templateCand.getId()  +","+templateCand.getTitle()+"," 
    				+","+templateCand.getFrequence()+"\n";
	    	
	    	buffer.append(templateValue);
	    }
	    
	  
	   TemporyDataHandler.store("templates", buffer.toString());
	}
	

}
