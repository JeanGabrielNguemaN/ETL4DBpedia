package br.ufrj.ppgi.greco.kettle.dbpedia.publication.steps;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.MappedPropertiesHandler;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class ArticleCheckerStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = ArticleCheckerStepMeta.class;

	private ArticleCheckerStepMeta meta;
	private SwtHelper swthlp;
	private String dialogTitle;

	//private TextVar wSelectedMappedProperties;
	private ComboVar wSelectedFieldForTitle;
	private Button 	wTitleUsedInInfoboxComparison;
	
	//NOVOS
	//private ComboVar wcPropertySelectionForTitle;
	//Atributos de infobox
	ColumnInfo columnInfoMappedProperties=null;
	ColumnInfo[] columns =null;
	
	private TableView wMapTable;

	private ComboVar wSelectedPropertyForInfoboxTitle;

	public ArticleCheckerStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		meta = (ArticleCheckerStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);
		
		//dialogTitle = "Verifica a existência de artigo na Wikipedia";
			//BaseMessages.getString(PKG, "Step.Title");
		dialogTitle = "Verificador de existência de Artigo";
	}

	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		ModifyListener lsMod = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				meta.setChanged();
			}
		};
		boolean changed = meta.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);

		shell.setText(this.dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Adiciona um label e um input text no topo do dialog shell
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "Step.StepNameField.Label"));
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
		Control lastControl = wStepname;
		
		
		
		// Adiciona
		Group wGroup0 = swthlp.appendGroup(shell, lastControl, BaseMessages.getString(PKG, "ArticleCheckerStep.Group.Title"));
		{
			wSelectedFieldForTitle = 
					swthlp.appendComboVarRow(wGroup0, null, 
							BaseMessages.getString(PKG, 
									"ArticleCheckerStep.Group.Title.Label"), lsMod);
			//Itens
			String[] camposPassosAnterior=getFields();
			
			wSelectedFieldForTitle.setItems(camposPassosAnterior);
		}

		lastControl=wGroup0;

		// Adiciona
		Group wGroup1 = swthlp.appendGroup(shell, lastControl, BaseMessages.getString(PKG, "ArticleCheckerStep.Group.Infobox"));
		{
			wSelectedPropertyForInfoboxTitle = swthlp
					.appendComboVarRow(wGroup1, null, BaseMessages
							.getString(PKG, "ArticleCheckerStep.Group.Infobox.Property.Label"), lsMod);
			
			
			 //Itens
			 String[] mappedMroperties=MappedPropertiesHandler.getMappedProperties();
			    
			 wSelectedPropertyForInfoboxTitle.setItems(mappedMroperties);
			
			 wSelectedPropertyForInfoboxTitle.addSelectionListener(
					 new SelectionAdapter() {
						 public void widgetSelected(SelectionEvent e) {
							 //Atualizar o comboVar
							 columnInfoMappedProperties.setComboValues(
									 MappedPropertiesHandler.getMappedProperties());
			    	    }
					}
			    );
			      
			 
			//wSelectedMappedProperties = swthlp.appendTextVarRow(wGroup1, wSelectedPropertyForTitle, BaseMessages.getString(PKG, "SparqlUpdateOutputStep.Graph.URI"), lsMod);
			
			wTitleUsedInInfoboxComparison = swthlp.appendCheckboxRow(wGroup1,  wSelectedPropertyForInfoboxTitle, BaseMessages
					.getString(PKG, "ArticleCheckerStep.Group.Infobox.Property.Use"),
					new SelectionListener() {

						public void widgetSelected(SelectionEvent arg0) {
							meta.setChanged();
						}

						public void widgetDefaultSelected(SelectionEvent arg0) {
							meta.setChanged();
						}
					});
		}

		
		lastControl=wGroup1;
		
		lastControl = buildContent(lastControl, lsMod);

		// Some buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "Step.Btn.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "Step.Btn.Cancel")); //$NON-NLS-1$

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

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wSelectedFieldForTitle.addSelectionListener(lsDef);
		//wSelectedMappedProperties.addSelectionListener(lsDef);
		
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
	
		try {
			wSelectedFieldForTitle.setText(Const.NVL(meta.getSelectedFieldForTitle(), ""));
			wSelectedPropertyForInfoboxTitle.setText(Const.NVL(meta.getSelectedPropertyForInfoboxTitle(), ""));
			wTitleUsedInInfoboxComparison.setSelection(meta.getTitleUsedInInfoboxComparison());
			
			//novo
			DataTable<String> table = meta.getMapTable();
			DataTable<String>.RowFactory rf = getRowFactory(table);

			for (int i = 0; i < table.size(); i++) {
				wMapTable.add(table.getRowRange(i, rf).getRow());
			}
			wMapTable.remove(0);
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

		try {
			// Pega dados da GUI e colocar no StepMeta
			DataTable<String> table = meta.getMapTable();
			table.clear();
			DataTable<String>.RowFactory rf = getRowFactory(table);
			
			for (int i = 0; i < wMapTable.getItemCount(); i++) {
				
				table.add(rf.newRow(wMapTable.getItem(i)).getFullRow());
			}

			//Armazenar dados do step
			meta.setSelectedFieldForTitle(wSelectedFieldForTitle.getText());
			
			meta.setSelectedPropertyForInfoboxTitle(wSelectedPropertyForInfoboxTitle.getText());
			meta.setTitleUsedInInfoboxComparison(wTitleUsedInInfoboxComparison.getSelection());
			
			//Armazena as propriedades escolhidas para a comparação
			String[] propriedadesSelecionadas = wMapTable.getItems(0);
			
			if(propriedadesSelecionadas!=null) {
				
				String properties="";
				
				for (String prop: propriedadesSelecionadas) {
					properties+=prop+",";
				}
				
				meta.setSelectedMappedProperties(properties);
				
			}
			
		} catch (NullPointerException e) {

		}

		dispose();
	}

	//---------------------------------------------------------------------------------------
	
	// Acrescenta widgets especificos na janela
	private Control buildContent(Control lastControl, ModifyListener defModListener) {
			
			//novo
			Group cpt = swthlp.appendGroup(shell, lastControl, BaseMessages
					.getString(PKG, "ArticleCheckerStep.Group.Properties"), 90);
		  
			//Atributos de infobox
			columnInfoMappedProperties=new ColumnInfo(BaseMessages
					.getString(PKG, "ArticleCheckerStep.Group.Properties.Selected"), ColumnInfo
					.COLUMN_TYPE_CCOMBO,  this.getInfoboxAtributes(wSelectedFieldForTitle.getText()));
		  
			//Lista vaizia no inicio
			String[] items={""};
			columnInfoMappedProperties.setComboValues(items);
				
			columns = new ColumnInfo[] {columnInfoMappedProperties};
			
			wMapTable = swthlp.appendTableView(cpt, null, columns, defModListener, 98);
			
			return cpt;
		}
	
	//Meu
   private String[] getInfoboxAtributes(String nome) {
		
		return MappedPropertiesHandler.getMappedProperties();
	}
   //
   private DataTable<String>.RowFactory getRowFactory(DataTable<String> table) {

		return table.newRowFactory(ArticleCheckerStepMeta.Field.MAP_TABLE_MAPPED_PROPERTY_FIELD_NAME.name());
   }
   
   /**
	 * Retorna nome de campos do passo anterior
	 * @return
	 */
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
}
