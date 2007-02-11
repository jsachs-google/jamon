package org.jamon.eclipse;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class JamonProjectPropertyPage extends PropertyPage {

	public JamonProjectPropertyPage() {
		super();
	}

	private Button isJamonProjectCheckbox;

	private IJavaProject getJavaProject() throws CoreException {
		return (IJavaProject) getProject().getNature(JavaCore.NATURE_ID);
	}

	private IProject getProject() {
		return (IProject) getElement().getAdapter(IProject.class);
	}

	private void addFirstSection(Composite parent) {
			Composite isJamonProjectGroup = new Composite(parent,SWT.NONE);
			isJamonProjectGroup.setLayout(new GridLayout(3, false));
			isJamonProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// project location entry field
			isJamonProjectCheckbox = new Button(isJamonProjectGroup, SWT.CHECK | SWT.LEFT);
			isJamonProjectCheckbox.setText("Is Jamon Project");
			isJamonProjectCheckbox.setEnabled(true);
			try {
				isJamonProjectCheckbox.setSelection(JamonNature.projectHasNature(getJavaProject().getProject()));
			}
			catch (CoreException ex) {
				ex.printStackTrace();
			}
            isJamonProjectCheckbox.addSelectionListener(isJamonListener);
	}

    private IsJamonListener isJamonListener = new IsJamonListener();
    
    private class IsJamonListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        
        void setEnabled() {
            final boolean enabled = isJamonProjectCheckbox.getSelection();
            sourceField.getControl().setEnabled(enabled);
            outputField.getControl().setEnabled(enabled);
        }
        
        public void widgetSelected(SelectionEvent e) {
            setEnabled();
        }
    }

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private Text templateSourceDirInput;
    private Text templateOutputDirInput;
    private DecoratedField sourceField;
    private DecoratedField outputField;

	private void addSecondSection(Composite parent) {
		final Composite composite = createDefaultComposite(parent);

        addTemplateSourceInput(composite);
		addTemplateOuputInput(composite);
		isJamonListener.setEnabled();
	}

    private class OutputModified implements ModifyListener {
        public void modifyText(ModifyEvent e) {
        }
    }
    
    private void addTemplateSourceInput(Composite composite) {
        Label templateSourceLabel = new Label(composite, SWT.NONE);
        templateSourceLabel.setText("Template source folder:");
        sourceField = new DecoratedField(composite, SWT.SINGLE | SWT.BORDER, new TextControlCreator());
        templateSourceDirInput = (Text) sourceField.getControl();
        String templateSourceDir = JamonNature.templateSourceFolderName(getProject());
        templateSourceDirInput.setText(
            (templateSourceDir != null)
            ? templateSourceDir
            : JamonNature.DEFAULT_TEMPLATE_SOURCE);
        
        GridData gd = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH + 
                                   FieldDecorationRegistry.getDefault().getMaximumDecorationWidth(), SWT.DEFAULT);
        sourceField.getLayoutControl().setLayoutData(gd);
        FieldDecoration requiredFieldIndicator = FieldDecorationRegistry.getDefault().
        getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
        sourceField.addFieldDecoration(requiredFieldIndicator, SWT.CENTER | SWT.LEFT, false);        
        templateSourceDirInput.addModifyListener(new SourceModified());
    }

    private class SourceModified implements ModifyListener {
        public void modifyText(ModifyEvent e) {
            String srcDir = templateSourceDirInput.getText();
            boolean ok = false;
            try {
                IFolder folder = getJavaProject().getProject().getFolder(new Path(srcDir));
                ok = folder.isAccessible();
            }
            catch (CoreException ex) {
                ex.printStackTrace();
            }
            FieldDecoration warning = FieldDecorationRegistry.getDefault().
            getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            if (!ok) {
                sourceField.addFieldDecoration(warning, SWT.CENTER | SWT.RIGHT, false);
            }
            else {
                sourceField.hideDecoration(warning);
            }
        }
    }
    
    private void addTemplateOuputInput(Composite composite) {
        Label templateOutputLabel = new Label(composite, SWT.NONE);
        templateOutputLabel.setText("Template output folder:");
        outputField = new DecoratedField(composite, SWT.SINGLE | SWT.BORDER, new TextControlCreator());
        templateOutputDirInput = (Text) outputField.getControl();
        String templateOutputDir = JamonNature.templateOutputFolderName(getProject());
        templateOutputDirInput.setText(
            (templateOutputDir != null)
            ? templateOutputDir
            : JamonNature.DEFAULT_OUTPUT_DIR);
        templateOutputDirInput.addModifyListener(new OutputModified());
        GridData gd = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH + 
                                   FieldDecorationRegistry.getDefault().getMaximumDecorationWidth(), SWT.DEFAULT);
        outputField.getLayoutControl().setLayoutData(gd);
        FieldDecoration requiredFieldIndicator = FieldDecorationRegistry.getDefault().
        getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
        outputField.addFieldDecoration(requiredFieldIndicator, SWT.CENTER | SWT.LEFT, false);        
        
    }

    @Override protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

    @Override protected void performDefaults() {
	}

    @Override public boolean performOk() {
		try {
			if (isJamonProjectCheckbox.getSelection()) {
				JamonNature.addToProject(getJavaProject().getProject(), templateSourceDirInput.getText(), templateOutputDirInput.getText());
			}
			else {
				JamonNature.removeFromProject(getJavaProject().getProject());
			}
		} catch(CoreException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

}