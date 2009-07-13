package org.jamon.eclipse.projectprefspage;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
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
import org.jamon.eclipse.JamonNature;

public class JamonProjectPropertyPage extends PropertyPage {

    public JamonProjectPropertyPage() {
        super();
    }

    private Button isJamonProjectCheckbox;

    private IJavaProject getJavaProject() throws CoreException {
        return (IJavaProject) getProject().getNature(JavaCore.NATURE_ID);
    }

    IProject getProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }

    void setJarIsValid(boolean jarIsValid) {
        setValid(jarIsValid);
        setErrorMessage(jarIsValid ? null : "the selected jar is not a jamon processor jar");
    }

    private void addFirstSection(Composite parent) {
        final Composite isJamonProjectGroup = new Composite(parent,SWT.NONE);
        isJamonProjectGroup.setLayout(new GridLayout(1, false));
        isJamonProjectGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

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

        processorSelector.render(isJamonProjectGroup);
    }

    private IsJamonListener isJamonListener = new IsJamonListener();

    private class IsJamonListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        void setEnabled() {
            final boolean enabled = isJamonProjectCheckbox.getSelection();
            templateSourceDirInput.setEnabled(enabled);
            templateOutputDirInput.setEnabled(enabled);
            processorSelector.setEnabled(enabled);
            if (!enabled) {
                setJarIsValid(true);
            }
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
    private ControlDecoration sourceField;
    private ProcessorSelector processorSelector = new ProcessorSelector(this);

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
        sourceField = new ControlDecoration(
            new Text(composite, SWT.SINGLE | SWT.BORDER),
            SWT.CENTER | SWT.LEFT, 
            composite);
        templateSourceDirInput = (Text) sourceField.getControl();
        String templateSourceDir = JamonNature.templateSourceFolderName(getProject());
        templateSourceDirInput.setText(
            (templateSourceDir != null)
            ? templateSourceDir
            : JamonNature.DEFAULT_TEMPLATE_SOURCE);

        setDecoratedTextInputLayout(sourceField);
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
            if (!ok) {
                sourceField.setImage(
                    FieldDecorationRegistry
                    .getDefault()
                    .getFieldDecoration(FieldDecorationRegistry.DEC_WARNING)
                    .getImage());
                sourceField.setDescriptionText("Folder does not exist");
                sourceField.show();
            }
            else {
                sourceField.hide();
            }
        }
    }

    private void addTemplateOuputInput(Composite composite) {
        Label templateOutputLabel = new Label(composite, SWT.NONE);
        templateOutputLabel.setText("Template output folder:");
        ControlDecoration outputField = new ControlDecoration(
            new Text(composite, SWT.SINGLE | SWT.BORDER),
            SWT.CENTER | SWT.LEFT, 
            composite);
        templateOutputDirInput = (Text) outputField.getControl();
        String templateOutputDir = JamonNature.templateOutputFolderName(getProject());
        templateOutputDirInput.setText(
            (templateOutputDir != null)
            ? templateOutputDir
            : JamonNature.DEFAULT_OUTPUT_DIR);
        templateOutputDirInput.addModifyListener(new OutputModified());
        setDecoratedTextInputLayout(outputField);
        FieldDecoration requiredFieldIndicator = FieldDecorationRegistry.getDefault().
        getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
        outputField.setImage(requiredFieldIndicator.getImage());
    }

    private void setDecoratedTextInputLayout(ControlDecoration decoration)
    {
        GridData gd = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH +
            FieldDecorationRegistry.getDefault().getMaximumDecorationWidth(), SWT.DEFAULT);
        gd.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();
        decoration.getControl().setLayoutData(gd);
    }

    @Override protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        composite.setLayoutData(gridData);

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
                JamonNature.addToProject(
                    getJavaProject().getProject(),
                    templateSourceDirInput.getText(),
                    templateOutputDirInput.getText(),
                    processorSelector.getProcessorJarLocations());
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

    /**
     * Return the first {@code IPath} element of an array of {@code IPath}, or {@code null} if there
     * is none.
     * @param paths the array of {@code IPath} elements
     * @return the first entry, or {@code null} if there is none.
     */
    static IPath extractFirstPathEntry(IPath[] paths)
    {
        return paths == null || paths.length == 0
            ? null
            : paths[0];
    }

}