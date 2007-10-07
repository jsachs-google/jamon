package org.jamon.eclipse.projectprefspage;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.swt.widgets.Shell;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;

/**
 * A {@code ProcessorChooser} for choosing a Jamon processor jar from outside the
 * project's workspace.
 */
class ExternalProcessorJarChooser extends ProvidedJarChooser {
    protected ExternalProcessorJarChooser(ProcessorSelector processorSelector) {
        super(processorSelector, ProcessorSourceType.EXTERNAL, "External Jar");
    }

    @Override protected boolean validate() {
        return validateJar(ProcessorJarLocations.externalPathToFile(getJarLocation()));
    }

    @Override protected IPath locateJar(Shell shell) {
        IPath[] paths = BuildPathDialogAccess.chooseExternalJAREntries(shell);
        return JamonProjectPropertyPage.extractFirstPathEntry(paths);
    }
}