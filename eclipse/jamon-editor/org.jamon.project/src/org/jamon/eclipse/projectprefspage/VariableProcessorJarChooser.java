/**
 *
 */
package org.jamon.eclipse.projectprefspage;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.swt.widgets.Shell;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;

/**
 * A {@code ProcessorChooser} for choosing a Jamon processor jar using a classpath variable.
 */
class VariableProcessorJarChooser extends ProvidedJarChooser {
    protected VariableProcessorJarChooser(ProcessorSelector processorSelector) {
        super(processorSelector, ProcessorSourceType.VARIABLE, "Variable");
    }

    @Override protected boolean validate() {
        return validateJar(ProcessorJarLocations.variablePathToFile(getJarLocation()));
    }

    @Override protected IPath locateJar(Shell shell) {
        IPath[] paths = BuildPathDialogAccess.chooseVariableEntries(shell, new IPath[0]);
        return JamonProjectPropertyPage.extractFirstPathEntry(paths);
    }
}