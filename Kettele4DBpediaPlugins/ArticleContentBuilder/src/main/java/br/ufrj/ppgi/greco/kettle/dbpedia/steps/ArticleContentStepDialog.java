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
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

/**
* This class is responsible for setting the Step User interface. 
* It displays many some fiedls and thus capture all user supplied inputs.
* 
* @version 1.01 03 Mar 2021
* @author Jean Gabriel Nguema Ngomo
*/
public class ArticleContentStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = ArticleContentBuilderStepMeta.class;

	private ArticleContentBuilderStepMeta meta;
	private SwtHelper swthlp;
	private String dialogTitle;

	//private TextVar wSelectedMappedProperties;
	private ComboVar wSelectedPropertyForTitle;
	private ComboVar wSelectedPropertyForIntroduction;
	
	//NOVOS
	//Atributos de infobox
	ColumnInfo columnInfoMappedProperties=null;
	ColumnInfo[] columns =null;
	
	public ArticleContentStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		meta = (ArticleContentBuilderStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);
		
		dialogTitle ="Construir o conteúdo do artigo Wikipedia";
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
		Group wGroup1 = swthlp.appendGroup(shell, lastControl, 
				BaseMessages.getString(PKG, "ArticleContentBuilderStep.group.title"));
		{
			
			wSelectedPropertyForTitle = 
					swthlp.appendComboVarRow(wGroup1, null, 
							BaseMessages.getString(PKG, 
									"ArticleContentBuilderStep.group.title"), lsMod);
	
			//Itens
			String[] camposPassosAnterior=getFields();
			
			wSelectedPropertyForTitle.setItems(camposPassosAnterior);
		
			wSelectedPropertyForIntroduction=
					swthlp.appendComboVarRow(wGroup1,  wSelectedPropertyForTitle, 
							BaseMessages.getString(PKG, 
									"ArticleContentBuilderStep.group.introduction"), lsMod);
	
			wSelectedPropertyForIntroduction.setItems(camposPassosAnterior);
			
		}

		lastControl=wGroup1;
		
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
		wSelectedPropertyForTitle.addSelectionListener(lsDef);
		wSelectedPropertyForIntroduction.addSelectionListener(lsDef);

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
			wSelectedPropertyForTitle.setText(Const.NVL(meta.getTitleField(), ""));
			wSelectedPropertyForIntroduction.setText(Const.NVL(meta.getFirstSectionField(), ""));
			
		
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
			
			meta.setTitleField(
					wSelectedPropertyForTitle.getText());
			
			meta.setFirstSectionField(
					wSelectedPropertyForIntroduction.getText());
			
		} catch (NullPointerException e) {

		}

		dispose();
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
