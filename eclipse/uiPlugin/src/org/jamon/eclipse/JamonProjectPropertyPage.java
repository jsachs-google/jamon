package org.jamon.eclipse;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
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
import org.eclipse.swt.widgets.Shell;
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

    private void setProcessorSourceType(ProcessorSourceType processorSourceType) {
        this.processorSourceType = processorSourceType;
        for (ProcessorSourceType option: ProcessorSourceType.values()) {
            processorChoosers.get(option).setEnabled(option == processorSourceType);
        }
    }

    private abstract class SimpleSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent event) {
            widgetSelected(event);
        }
    }

    private abstract class ProcessorChoice {
        private final String radioLabel;
        private Button radio;
        private final ProcessorSourceType processorSourceType;
        public abstract void setEnabled(boolean enabled);
        protected void render(Composite parent) {
            radio = new Button(parent, SWT.RADIO);
            radio.setText(radioLabel);
            radio.addSelectionListener(new SimpleSelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    if (radio.getSelection()) {
                        setProcessorSourceType(processorSourceType);
                    }
                }
            });
        }

        protected ProcessorChoice(ProcessorSourceType processorSourceType, String radioLabel) {
            this.radioLabel = radioLabel;
            this.processorSourceType = processorSourceType;
        }
    }

    private abstract class ProcessorJarChoice extends ProcessorChoice {
        protected ProcessorJarChoice(ProcessorSourceType processorSourceType, String radioLabel) {
            super(processorSourceType, radioLabel);
        }

        private IPath jarLocation;
        private Text textField;
        private Button jarChooserButton;

        /**
         * Pop up a dialog to locate the appropriate kind of jar
         * @param shell the {@code Shell} to use for the dialog
         * @return the path of the selected jar
         */
        protected abstract IPath locateJar(Shell shell);

        /**
         * Validate currently selected jar.
         * @return true if the current selection is a valid processor jar
         */
        protected abstract boolean validate();

        private void setJarLocation(IPath path) {
            jarLocation = path;
            textField.setText(path == null ? "" : path.toPortableString());
        }

        protected void render(final Composite parent, IPath jarLocation) {
            super.render(parent);
            textField = new Text(parent, SWT.READ_ONLY);
            textField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            setJarLocation(jarLocation);

            jarChooserButton = new Button(parent, SWT.PUSH);
            jarChooserButton.setText("Choose jar");
            jarChooserButton.addSelectionListener(new SimpleSelectionListener() {
                public void widgetSelected(SelectionEvent selectionEvent) {
                    setJarLocation(locateJar(parent.getShell()));
                }
            });
        }

        public IPath getJarLocation() {
            return jarLocation;
        }

        @Override public void setEnabled(boolean enabled) {
            textField.setEnabled(enabled);
            jarChooserButton.setEnabled(enabled);
            if (enabled == true) {
                boolean jarIsValid = validate();
                setValid(jarIsValid);
                setErrorMessage(
                    jarIsValid ? null : "the selected jar is not a jamon processor jar");
            }
        }

        protected boolean validateJar(File jar) {
            EclipseUtils.logInfo("checking on file " + jar.toString());
            return jar.exists()
                && jar.getName().startsWith("jamon-processor"); // FIXME
        }
    }

    private class WorkspaceProcessorJarChoice extends ProcessorJarChoice {
        protected WorkspaceProcessorJarChoice() {
            super(ProcessorSourceType.WORKSPACE, "Jar in workspace");
        }

        @Override protected boolean validate() {
            return validateJar(new File(
                getProject().getWorkspace().getRoot().getLocation().append(
                    getJarLocation())
                    .toOSString()));
        }

        @Override protected IPath locateJar(Shell shell) {

            IPath[] paths = BuildPathDialogAccess.chooseJAREntries(
                shell, null, new IPath[] { getJarLocation() });
            return paths.length > 0 ? paths[0] : null;
        }
    }

    private class ExternalProcessorJarChoice extends ProcessorJarChoice {
        protected ExternalProcessorJarChoice() {
            super(ProcessorSourceType.EXTERNAL, "External Jar");
        }

        @Override protected boolean validate() {
            return validateJar(new File(getJarLocation().toOSString()));
        }

        @Override protected IPath locateJar(Shell shell) {

            IPath[] paths = BuildPathDialogAccess.chooseExternalJAREntries(shell);
            return paths.length > 0 ? paths[0] : null;
        }
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

        processorChoosers.put(ProcessorSourceType.WORKSPACE, workspaceProcessorJarChoice);
        processorChoosers.put(ProcessorSourceType.EXTERNAL, externalProcessorJarChoice);

        workspaceProcessorJarChoice.render(
            isJamonProjectGroup, JamonNature.workspaceJar(getProject()));
        externalProcessorJarChoice.render(
            isJamonProjectGroup, JamonNature.externalJar(getProject()));
        setProcessorSourceType(JamonNature.processorSourceType(getProject()));
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
    private ProcessorSourceType processorSourceType;
    private Map<ProcessorSourceType, ProcessorChoice> processorChoosers =
        new EnumMap<ProcessorSourceType, ProcessorChoice>(ProcessorSourceType.class);
    private final ExternalProcessorJarChoice externalProcessorJarChoice =
        new ExternalProcessorJarChoice();
    private final WorkspaceProcessorJarChoice workspaceProcessorJarChoice =
        new WorkspaceProcessorJarChoice();

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
                    processorSourceType,
                    workspaceProcessorJarChoice.getJarLocation(),
                    externalProcessorJarChoice.getJarLocation());
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