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

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Step(
		  id = "TemplateMapper",
		  name = "Template Mapper",
		  description = "Seleciona um Template de Infobox e mapea sua propriedades para campos de um domínio",
		  image = "br/ufrj/ppgi/greco/kettle/dbpedia/steps/resources/image.jpg",
		  categoryDescription = "LinkedDataBR",
		  documentationUrl = "https://github.com/jgnn/TemplateMapper.git"
		)

public class TemplateMapperStepMeta extends BaseStepMeta implements StepMetaInterface {
	
	// Fields for serialization
	public enum Field {
		DATA_ROOT_NODE, 
		VERSION,
		// Para a Aba 'Mapeamento'
		MAP_TABLE,
		MAP_TABLE_DOMAIN_FIELD_FIELD_NAME, 
		MAP_TABLE_TEMPLATE_PROPERTY_FIELD_NAME, 
		//entradas
	    SELECTED_TEMPLATE,
		// Aba 'Campos de saida'
		MAPPED_DOMAIN_FIELD_OUT_FIELD_NAME, 
		MAPPED_TEMPLATE_PROPERTY_OUT_FIELD_NAME
	}

	// Campos do step
	private DataTable<String> mapTable;
	
	private String mappedDomainFieldOuputFieldName;
	
	private String mappedTemplatePropertyOutputFieldName;

	private String selectedTemplale;

	public TemplateMapperStepMeta() {
		setDefault();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {

		// if (Const.isEmpty(fieldName)) {
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
		return new TemplateMapperStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new TemplateMapperStepData();
	}

	@Override
	public String getDialogClassName() {
		return TemplateMapperStepDialog.class.getName();
	}

	// Carregar campos a partir do XML de um .ktr
	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {

		try {
			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());
			
			mapTable = (DataTable<String>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.MAP_TABLE.name()));
			mappedDomainFieldOuputFieldName = (String) XMLHandler.getTagValue(stepDomNode, Field.MAPPED_DOMAIN_FIELD_OUT_FIELD_NAME.name());
			mappedTemplatePropertyOutputFieldName = (String) XMLHandler.getTagValue(stepDomNode, Field.MAPPED_TEMPLATE_PROPERTY_OUT_FIELD_NAME.name());
		
			selectedTemplale = XMLHandler.getTagValue(stepDomNode, 
									Field.SELECTED_TEMPLATE.name());
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		XStream xs = new XStream(new DomDriver());
		xs.alias("DataTable", DataTable.class);
		xs.registerConverter(new DataTableConverter());
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XMLHandler.addTagValue(Field.MAP_TABLE.name(), xs.toXML(mapTable)));
		xml.append(XMLHandler.addTagValue(Field.MAPPED_DOMAIN_FIELD_OUT_FIELD_NAME.name(), mappedDomainFieldOuputFieldName));
		xml.append(XMLHandler.addTagValue(Field.MAPPED_TEMPLATE_PROPERTY_OUT_FIELD_NAME.name(), mappedTemplatePropertyOutputFieldName ));

		xml.append(XMLHandler.addTagValue(Field.SELECTED_TEMPLATE.name(), selectedTemplale));
				
		return xml.toString();
	}

	/**
	 * Carregar campos a partir do repositorio
	 */
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		
		try {
			int version = (int) repository.getStepAttributeInteger(stepIdInRepository, Field.VERSION.name());

			switch (version) {
			case 1:
				int nrLines = (int) repository.getStepAttributeInteger(stepIdInRepository, "nr_lines");
				
				mapTable = new DataTable<String>(Field.MAP_TABLE.name()
						 	,Field.MAP_TABLE_DOMAIN_FIELD_FIELD_NAME.name()
						 	,Field.MAP_TABLE_TEMPLATE_PROPERTY_FIELD_NAME.name()
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

				 
				mappedDomainFieldOuputFieldName = repository.getStepAttributeString(stepIdInRepository,
						Field.MAPPED_DOMAIN_FIELD_OUT_FIELD_NAME.name());
				
				mappedTemplatePropertyOutputFieldName= repository.getStepAttributeString(stepIdInRepository,
						Field.MAPPED_TEMPLATE_PROPERTY_OUT_FIELD_NAME.name());

				selectedTemplale= repository.getStepAttributeString(stepIdInRepository,
						Field.SELECTED_TEMPLATE.name());
				
				break;
			default:
				setDefault();
			}
		} catch (Exception e) {
			throw new KettleException(
					"Unable to read step information from the repository for id_step=" + stepIdInRepository, e);
		}

	}

	// Rogers (2012): Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {
		try {
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.VERSION.name(), 1);

			// Map Table
			int linhas = (int) mapTable.size();
			int colunas = mapTable.getHeader().size();
			repository.saveStepAttribute(idOfTransformation, idOfStep, "nr_lines", linhas);
			for (int i = 0; i < linhas; i++) {
				for (int f = 0; f < colunas; f++) {
					repository.saveStepAttribute(idOfTransformation, idOfStep, i, mapTable.getHeader().get(f),
							mapTable.getValue(i, f));
				}
			}

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.MAPPED_DOMAIN_FIELD_OUT_FIELD_NAME.name(),
					mappedDomainFieldOuputFieldName);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.MAPPED_TEMPLATE_PROPERTY_OUT_FIELD_NAME.name(),
					mappedTemplatePropertyOutputFieldName);
			
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.SELECTED_TEMPLATE.name(),
					selectedTemplale);

		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + idOfStep, e);
		}
	}

	public void setDefault() {

		mapTable = new DataTable<String>(Field.MAP_TABLE.name()
				,Field.MAP_TABLE_DOMAIN_FIELD_FIELD_NAME.name()
				,Field.MAP_TABLE_TEMPLATE_PROPERTY_FIELD_NAME.name()
				);
	
		mappedDomainFieldOuputFieldName = "templateMappings";
		mappedTemplatePropertyOutputFieldName = "propriedadeTemplateMapeada";
		selectedTemplale="";
	
	}

	/**
	 * It describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		
		ValueMetaInterface field = 
				new ValueMetaString(mappedDomainFieldOuputFieldName);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
	}

	// Getters & Setters
	public DataTable<String> getMapTable() {
		return mapTable;
	}
	
	public String getMappedDomainFieldOuputFieldName() {
		return mappedDomainFieldOuputFieldName;
	}

	public void setMappedDomainFieldOuputFieldName(String mappedDomainFieldOuputFieldName) {
		this.mappedDomainFieldOuputFieldName = mappedDomainFieldOuputFieldName;
	}

	public String getMappedTemplatePropertyOutputFieldName() {
		return mappedTemplatePropertyOutputFieldName;
	}

	public void setMappedTemplatePropertyOutputFieldName(String mappedTemplatePropertyOutputFieldName) {
		this.mappedTemplatePropertyOutputFieldName = mappedTemplatePropertyOutputFieldName;
	}
	
	public String getSelectedTemplale() {
		return selectedTemplale;
	}

	public void setSelectedTemplale(String selectedTemplale) {
		this.selectedTemplale = selectedTemplale;
	}

}
