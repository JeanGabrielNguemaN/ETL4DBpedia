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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.pentaho.di.ui.core.widget.PasswordTextVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import br.ufrj.ppgi.greco.kettle.dbpedia.utils.MappedPropertiesHandler;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

/**
* This class is responsible for setting the Step User interface. 
* It displays many some fiedls and thus capture all user supplied inputs.
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class ArticlePublisherStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = ArticlePublisherStepMeta.class;

	private ArticlePublisherStepMeta meta;
	private SwtHelper swthlp;
	private String dialogTitle;

	//private TextVar wSelectedMappedProperties;
	private TextVar wEndPoint;
	 
	private TextVar wBotUser;
	
	private TextVar wBotPassword;
	
	//private PasswordTextVar wBotPassword;
	
	private TextVar wSummary;
	
	//NOVOS
	//Atributos de infobox
	ColumnInfo columnInfoMappedProperties=null;
	ColumnInfo[] columns =null;

	private Button wCreateOnly;

	private TextVar wURL;

	private TextVar wTitle;

	private TextVar wAuthor;

	private TextVar wPublisher;

	private TextVar wDate;

	private TextVar wAccessDate;

	private TextVar wEpm;

	private TextVar wCategories;

	private Button wIsTestPhase;
	
	public ArticlePublisherStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		meta = (ArticlePublisherStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);
		
		dialogTitle ="Criar ou Atualizar artigo na Wikipedia";
		//BaseMessages.getString(PKG, "Step.Title");
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
		/*
		Group wGroup1 = swthlp.appendGroup(shell, lastControl, 
				BaseMessages.getString(PKG, "ArticlePublisherStep.group.title"));
		{
			
			wEndPoint=swthlp.appendTextVarRow(wGroup1, null, BaseMessages.getString(PKG, "ArticlePublisherStep.Group.Keywords"), lsMod);
	
			wBotUser=
					swthlp.appendTextVarRow(wGroup1,  wEndPoint, 
							 BaseMessages.getString(PKG, "ArticlePublisherStep.Group.User"), lsMod);
			wBotPassword=
					swthlp.appendTextVarRow(wGroup1,  wBotUser, 
							 BaseMessages.getString(PKG, "ArticlePublisherStep.Group.Password"), lsMod);
			
			wSummary=
					swthlp.appendTextVarRow(wGroup1,  wBotPassword, 
							 BaseMessages.getString(PKG, "ArticlePublisherStep.Group.Summary"), lsMod);
							
			
		}

		lastControl=wGroup1;
		*/
		//construir o restante da interface
		lastControl = buildContents(lastControl, lsMod);
		
		// Buttons OK e Cancel
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
		
		wEndPoint.addSelectionListener(lsDef);
		wBotUser.addSelectionListener(lsDef);
		wBotPassword.addSelectionListener(lsDef);
		wSummary.addSelectionListener(lsDef);

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
			wEndPoint.setText(Const.NVL(meta.getEndPoint(), ""));
			wBotUser.setText(Const.NVL(meta.getBotUser(), ""));
			wBotPassword.setText(Const.NVL(meta.getBotPassword(), ""));
			
			wIsTestPhase.setSelection(meta.isTestPhase());
			wEpm.setText(meta.getEpm());
			
			wSummary.setText(Const.NVL(meta.getSummary(), ""));
			
			wCategories.setText(Const.NVL(meta.getCategories(), ""));
			wCreateOnly.setSelection(meta.isCreateOnly());
			
			//References
			wURL.setText(Const.NVL(meta.getRefUrl(), ""));
			wTitle.setText(Const.NVL(meta.getRefTitle(), ""));
			wAuthor.setText(Const.NVL(meta.getRefAuthor(), ""));
			wPublisher.setText(Const.NVL(meta.getRefPublisher(), ""));
			wDate.setText(Const.NVL(meta.getRefDate(), ""));
			wAccessDate.setText(Const.NVL(meta.getRefAccessDate(), ""));
			
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
			
			meta.setEndPoint(wEndPoint.getText());
			meta.setBotUser(wBotUser.getText());
			meta.setBotPassword(wBotPassword.getText());
			
			meta.setEpm(wEpm.getText());
			meta.setTestPhase(wIsTestPhase.getSelection());
			
			meta.setSummary(wSummary.getText());
			meta.setCategories(wCategories.getText());
			meta.setCreateOnly(wCreateOnly.getSelection());
			
			//Reference
			meta.setRefUrl(wURL.getText());
			meta.setRefTitle(wTitle.getText());
			meta.setRefAuthor(wAuthor.getText());
			meta.setRefPublisher(wPublisher.getText());
			meta.setRefDate(wDate.getText());
			meta.setRefAccessDate(wAccessDate.getText());
		
		} catch (NullPointerException e) {

		}

		dispose();
	}
	
	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);

		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "ArticlePubliserStep.Tab.Connection"));
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);
		
		Control lastControl2 = null;
		//-------
		//wEndPoint = swthlp.appendTextVarRow(cpt, null
		//		,BaseMessages.getString(PKG, "ArticlePubliserStep.Tab.Connection.EndPoint")
		//				,defModListener);
		
		
		Group wGroup1 = swthlp.appendGroup(cpt, null, 
				BaseMessages.getString(PKG, "ArticlePublisherStep.Tab.Connection.group.title"));
		{
			wEndPoint = swthlp.appendTextVarRow(wGroup1, null,BaseMessages.getString(
					PKG, "ArticlePubliserStep.Tab.Connection.EndPoint"),defModListener);
			
			wBotUser = swthlp.appendTextVarRow(wGroup1, wEndPoint, BaseMessages.getString(
					PKG, "ArticlePubliserStep.Tab.Connection.User"),defModListener);
			
			wBotPassword = swthlp.appendTextVarRow(wGroup1, wBotUser, BaseMessages.getString(
					PKG, "ArticlePubliserStep.Tab.Connection.Password"),defModListener);
		}

		Group wGroup2 = swthlp.appendGroup(cpt, wGroup1, 
				BaseMessages.getString(PKG, "ArticlePublisherStep.Tab.Connection.Ambiente.title"));
		{
			
			wEpm = swthlp.appendTextVarRow(wGroup2, wCreateOnly,BaseMessages.getString(
					PKG, "ArticlePublisherStep.Tab.Connection.Ambiente.EPM"),defModListener);
			
			wIsTestPhase = swthlp.appendCheckboxRow(wGroup2, wEpm, BaseMessages.getString(
					PKG, "ArticlePublisherStep.Tab.Connection.Ambiente.TestPhase")
					, new SelectionListener() {
				
						public void widgetDefaultSelected(SelectionEvent arg0) {
							widgetSelected(arg0);
						}
						
						public void widgetSelected(SelectionEvent e) {
							meta.setChanged(true);
						}
					});
		}
		
		
		Group wGroup3 = swthlp.appendGroup(cpt, wGroup2, 
				BaseMessages.getString(PKG, "ArticlePublisherStep.Tab.Connection.ParamsGroup.title"));
		{
			wCreateOnly = swthlp.appendCheckboxRow(wGroup3, null, BaseMessages.getString(
					PKG, "ArticlePubliserStep.Tab.Connection.CreateOnly"), new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent arg0) {
							widgetSelected(arg0);
						}
						
						public void widgetSelected(SelectionEvent e) {
							meta.setChanged(true);
						}
					});
			
			wSummary = swthlp.appendTextVarRow(wGroup3, wCreateOnly,BaseMessages.getString(
					PKG, "ArticlePubliserStep.Tab.Connection.Summary"), defModListener);
			
			wCategories = swthlp.appendTextVarRow(wGroup3, wSummary, BaseMessages.getString(
					PKG, "ArticlePublisherStep.Tab.Connection.ParamsGroup.Category"), defModListener);
			
		}
		
		item.setControl(cpt);
		
		//Tab parâmetros de referências
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "ArticlePubliserStep.Tab.Reference"));
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		
		wURL = swthlp.appendTextVarRow(cpt, null,BaseMessages.getString(PKG, 
				   "ArticlePubliserStep.Tab.Reference.Url"),defModListener);
		
		wTitle = swthlp.appendTextVarRow(cpt, wURL,BaseMessages.getString(PKG, 
					"ArticlePubliserStep.Tab.Reference.Title"),defModListener);
		
		wAuthor = swthlp.appendTextVarRow(cpt, wTitle,BaseMessages.getString(PKG, 
					"ArticlePubliserStep.Tab.Reference.Author"),defModListener);
		
		wPublisher =swthlp.appendTextVarRow(cpt, wAuthor,BaseMessages.getString(PKG, 
						"ArticlePubliserStep.Tab.Reference.Publisher"),defModListener);
		
		wDate = swthlp.appendTextVarRow(cpt, wPublisher,BaseMessages.getString(PKG, 
						"ArticlePubliserStep.Tab.Reference.Date"),defModListener);
		
		wAccessDate = swthlp.appendTextVarRow(cpt, wDate,BaseMessages.getString(PKG, 
						"ArticlePubliserStep.Tab.Reference.AccessDate"),defModListener);
		
		item.setControl(cpt);

		wTabFolder.setSelection(0);

		return wTabFolder;
	}
}
