/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 *
 */
package org.jamon.eclipse.projectprefspage;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;

/**
 * A {@code ProcessorChooser} which represents the choice of using the processor jar provided by the
 * org.jamon.processor plugin.
 */
class PluginProcessorChooser extends ProcessorChooser {
    protected PluginProcessorChooser(ProcessorSelector processorSelector)
    {
        super(processorSelector, ProcessorSourceType.PLUGIN, "Plugin-provided");
    }

    private Label label;
    private final File pluginProcessorJar = ProcessorJarLocations.getPluginProcessorJar();

    @Override public void setEnabled(boolean p_enabled)
    {
        label.setEnabled(p_enabled);
        if (p_enabled) {
            //FIXME - verify?
            getProcessorSelector().setJarIsValid(true);
        }
    }

    @Override
    void setWidgetEnabled(boolean enabled) {
        // don't enable if there is no processor plugin
        super.setWidgetEnabled(enabled && pluginProcessorJar != null);
    }

    @Override protected void render(Composite p_parent, ProcessorJarLocations p_jarLocations)
    {
        super.render(p_parent, p_jarLocations);
        label = new Label(p_parent, SWT.NONE);
        String pluginProcessorJarName = pluginProcessorJar != null
            ? pluginProcessorJar.getName()
            : "N/A";
        label.setText(pluginProcessorJarName);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);
    }
}