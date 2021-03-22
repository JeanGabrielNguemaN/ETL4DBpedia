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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.TemplatesHandler;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

/**
* This class is responsible for setting the Step User interface. 
* It displays many some fiedls and thus capture all user supplied inputs.
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class TemplateMapperStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = TemplateMapperStepMeta.class;

	private TemplateMapperStepMeta meta;
	private SwtHelper swthlp;
	private String dialogTitle;

	// Variaveis dos widgets
	private Label wlTemplateSelection;
	private FormData fdlTemplateSelection;
	private ComboVar wcTemplateSelection;
	private FormData fdcTemplateSelection;
	
	private TextVar wMappedDomainFieldOutputFieldName;
	private TextVar wMappedTemplatePropertyOutputFieldName;
	
	private TableView wMapTable;

	

	//Campos do dominio
    ColumnInfo columnInfoDomainFiels =null;
	//Atributos de infobox
	ColumnInfo columnInfoTemplatesProperties=null;
	//Colunas mapeamento
	ColumnInfo[] columns =null;

	public TemplateMapperStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		meta = (TemplateMapperStepMeta) baseStepMeta;
		
		swthlp = new SwtHelper(transMeta, this.props);

		// Additional initialization here
		dialogTitle = 
				//BaseMessages.getString(PKG, "TemplateMapperStep.Title");
				"Alinhador Campos -> Propriedades do Template";
	}

	// Acrescenta widgets especificos na janela
	private Control buildContent(Control lastControl, ModifyListener defModListener) {
		
		//novo
		Group cpt = swthlp.appendGroup(shell, lastControl, BaseMessages.getString(PKG, "TemplateMapperStep.Tab.Mapping"), 90);
	   
		//Campos do domino
	    columnInfoDomainFiels =new ColumnInfo(
	    				BaseMessages.getString(PKG, "TemplateMapperStep.Tab.Mapping.ColunaCampo"), 
	    				ColumnInfo.COLUMN_TYPE_CCOMBO, 
	    				this.getFields(), true);
	    
		//Atributos de infobox
		columnInfoTemplatesProperties=new ColumnInfo(
						BaseMessages.getString(PKG, "TemplateMapperStep.Tab.Mapping.ColunaAtrInfobox"),
						ColumnInfo.COLUMN_TYPE_CCOMBO,  
						this.getInfoboxAtributes(wcTemplateSelection.getText()));
	  
		     //Lista vaizia no inicio
		     String[] items={""};
		     columnInfoTemplatesProperties.setComboValues(items);
			
		columns = new ColumnInfo[] {columnInfoDomainFiels , columnInfoTemplatesProperties};
			
				
		wMapTable = swthlp.appendTableView(cpt, null, columns, defModListener, 98);
		
		return cpt;
	}

	private String[] getFields() {
		return getFields(-1);
	}

	/**
	 * Retorna nome de campos do passo anterior
	 * @param type (inteiro, -1)
	 * @return
	 */
	private String[] getFields(int type) {

		List<String> result = new ArrayList<String>();

		try {
			//JGNN (Comentario apenas): Obter campos do passo anterior
			RowMetaInterface inRowMeta = this.transMeta.getPrevStepFields(stepname);

			List<ValueMetaInterface> fields = inRowMeta.getValueMetaList();

			for (ValueMetaInterface field : fields) {
				if (field.getType() == type || type == -1)
					result.add(field.getName());
			}

		} catch (KettleStepException e) {
			e.printStackTrace();
		}

		return result.toArray(new String[result.size()]);
	}
	
	
	private String[] getInfoboxAtributes(String nome) {
		
		String[] infoboxPropertiesArray = TemplatesHandler.getTemplatesProperties(nome);
		
		//Sort array elements
		Arrays.sort(infoboxPropertiesArray);
		
		return infoboxPropertiesArray;
	}

	// Adiciona listeners para widgets tratarem Enter
	// The will close the window affirmatively when the user press Enter in one
	// of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		//wSubjectOutputFieldName.addSelectionListener(lsDef);
		//wPredicateOutputFieldName.addSelectionListener(lsDef);
		//wObjectOutputFieldName.addSelectionListener(lsDef);
	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		// ModifyListener padrao
		ModifyListener lsMod = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};
		boolean changed = meta.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);

		shell.setText(dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Adiciona um label e um input text no topo do dialog shell
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "TemplateMapperStep.StepNameField.Label"));
		props.setLook(wlStepname);

		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);

		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		//Control lastControl = wStepname; 
		
		//******************************************************************************
	     
		// Adiciona label e combo do campo Literal ou Objeto?
		wlTemplateSelection = new Label(shell, SWT.RIGHT);
		
		wlTemplateSelection.setText(BaseMessages.getString(PKG,
		              "TemplateMapperStep.InfoboxSelection.Label"));
		
		props.setLook(wlTemplateSelection);
		      
		//formData Layout
		fdlTemplateSelection = new FormData();
		fdlTemplateSelection.left = new FormAttachment(0, 0);
		fdlTemplateSelection.top = new FormAttachment(wStepname, margin);
		fdlTemplateSelection.right = new FormAttachment(middle, -margin);
		wlTemplateSelection.setLayoutData(fdlTemplateSelection);

		wcTemplateSelection = new ComboVar(transMeta, shell, SWT.SINGLE | SWT.LEFT
		              | SWT.BORDER);
		
		props.setLook(wcTemplateSelection);
		
		wcTemplateSelection.addModifyListener(lsMod);
		      
		//formData Combo
		fdcTemplateSelection = new FormData();
		fdcTemplateSelection.left = new FormAttachment(middle, 0);
		fdcTemplateSelection.right = new FormAttachment(100, 0);
		fdcTemplateSelection.top = new FormAttachment(wStepname, margin);
		wcTemplateSelection.setLayoutData(fdcTemplateSelection);
		      
		      //Itens
		String[] items=TemplatesHandler.getTemplates();
		      
		wcTemplateSelection.setItems(items);
		      //wcTemplateSelection.select(0);
		
		wcTemplateSelection.addModifyListener(lsMod);
		      
		Control lastControl = wcTemplateSelection;
		
		wcTemplateSelection.addSelectionListener(
		    		  new SelectionAdapter() {
	
		    			  public void widgetSelected(SelectionEvent e) {
		    				  //Atualizar o comboVar
		    				  columnInfoTemplatesProperties.setComboValues(
		    						  getInfoboxAtributes(wcTemplateSelection.getText()));
		    			  }
					}
		    		  ); 
		 //******************************************************************************

		// Chama metodo que adiciona os widgets especificos da janela
		lastControl = buildContent(lastControl, lsMod);

		// Bottom buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "TemplateMapperStep.Btn.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "TemplateMapperStep.Btn.Cancel")); //$NON-NLS-1$
		setButtonPositions(new Button[] { wOK, wCancel }, margin, lastControl);

		// Add listeners
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		// It closes the window affirmatively when the user press enter in one
		// of the text input fields
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
		wStepname.addSelectionListener(lsDef);
		addSelectionListenerToControls(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Populate the data of the controls
		getData();

		// Set the shell size, based upon previous time...
		setSize();

		meta.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	private void getData() {
		wStepname.selectAll();

		// Recupera dados do StepMeta e adiciona na GUI
		try {
			DataTable<String> table = meta.getMapTable();
			DataTable<String>.RowFactory rf = getRowFactory(table);

			for (int i = 0; i < table.size(); i++) {
				wMapTable.add(table.getRowRange(i, rf).getRow());
			}
			wMapTable.remove(0);

			wcTemplateSelection.setText(Const.NVL(meta.getSelectedTemplale(), ""));
			
			wMappedDomainFieldOutputFieldName.setText(Const.NVL(meta.getMappedDomainFieldOuputFieldName(), ""));
			wMappedTemplatePropertyOutputFieldName.setText(Const.NVL(meta.getMappedTemplatePropertyOutputFieldName(), ""));
			
		} catch (NullPointerException e) {

		}
	}

	protected void cancel() {
		stepname = null;
		meta.setChanged(changed);
		dispose();
	}

	protected void ok() {
		if (StringUtil.isEmpty(wStepname.getText()))
			return;

		stepname = wStepname.getText(); // return value

		// Pega dados da GUI e colocar no StepMeta
		DataTable<String> table = meta.getMapTable();
		table.clear();
		DataTable<String>.RowFactory rf = getRowFactory(table);

		//List<String[]> rowsList= new ArrayList<String[]>();
		
		
		for (int i = 0; i < wMapTable.getItemCount(); i++) {
			
			table.add(rf.newRow(wMapTable.getItem(i)).getFullRow());
			//rowsList.add(wMapTable.getItem(i));
		}

		//Armazenar dados do step
		meta.setSelectedTemplale(
				wcTemplateSelection.getText());	
		
		// Fecha janela
		dispose();
	}

	private DataTable<String>.RowFactory getRowFactory(DataTable<String> table) {
		
		return table.newRowFactory(TemplateMapperStepMeta.Field.MAP_TABLE_DOMAIN_FIELD_FIELD_NAME.name(),
				TemplateMapperStepMeta.Field.MAP_TABLE_TEMPLATE_PROPERTY_FIELD_NAME.name());
	}
}
