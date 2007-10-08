/**
 *
 */
package org.jamon.eclipse.projectprefspage;

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
        // TODO Auto-generated constructor stub
    }
    private Label label;
    @Override public void setEnabled(boolean p_enabled)
    {
        label.setEnabled(p_enabled);
        if (p_enabled) {
            //FIXME - verify?
            getProcessorSelector().setJarIsValid(true);
        }
    }
    @Override protected void render(Composite p_parent, ProcessorJarLocations p_jarLocations)
    {
        super.render(p_parent, p_jarLocations);
        label = new Label(p_parent, SWT.NONE);
        label.setText(ProcessorJarLocations.getPluginProcessorJar().getName());
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);
    }
}