/**
 *
 */
package org.jamon.eclipse.projectprefspage;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;
import org.jamon.eclipse.ProjectClassLoader;

/**
 * A {@code ProcessorChooser} for which the user must provide the location of a jamon processor jar.
 */
abstract class ProvidedJarChooser extends ProcessorChooser {
    protected ProvidedJarChooser(
        ProcessorSelector procesorSelector,
        ProcessorSourceType processorSourceType,
        String radioLabel) {
        super(procesorSelector, processorSourceType, radioLabel);
        if (processorSourceType == ProcessorSourceType.PLUGIN) {
            throw new IllegalArgumentException(
                "Cannot provide a jar for ProcessorSourceType PLUGIN");
        }
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

    @Override protected void render(final Composite parent, ProcessorJarLocations p_jarLocations) {
        super.render(parent, p_jarLocations);
        textField = new Text(parent, SWT.READ_ONLY);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        setJarLocation(p_jarLocations.getLocationForSourceType(getProcessorSourceType()));

        jarChooserButton = new Button(parent, SWT.PUSH);
        jarChooserButton.setText("Choose jar");
        jarChooserButton.addSelectionListener(new SimpleSelectionListener() {
            public void widgetSelected(SelectionEvent selectionEvent) {
                setJarLocation(locateJar(parent.getShell()));
                checkJarChoiceValidity();
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
            checkJarChoiceValidity();
        }
    }

    private void checkJarChoiceValidity() {
        getProcessorSelector().setJarIsValid(jarLocation != null && validate());
    }

    protected boolean validateJar(File jar) {
        return jar.exists() && ProjectClassLoader.verifyProcessorJar(jar);
    }
}