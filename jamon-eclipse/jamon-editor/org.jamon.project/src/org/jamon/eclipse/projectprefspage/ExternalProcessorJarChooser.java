/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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