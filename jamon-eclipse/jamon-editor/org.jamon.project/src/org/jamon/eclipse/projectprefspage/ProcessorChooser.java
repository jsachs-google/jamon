/**
 *
 */
package org.jamon.eclipse.projectprefspage;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;


/**
 * A widget for choosing where to get the jamon processor from.
 */
abstract class ProcessorChooser {
    private final ProcessorSelector processorSelector;
    private final String radioLabel;
    private Button radio;
    private final ProcessorSourceType processorSourceType;
    abstract void setEnabled(boolean enabled);

    void setWidgetEnabled(boolean enabled) {
        radio.setEnabled(enabled);
        if (enabled == false) {
            setEnabled(false);
        }
    }

    protected void render(Composite p_parent, ProcessorJarLocations p_jarLocations) {
        radio = new Button(p_parent, SWT.RADIO);
        radio.setText(radioLabel);
        radio.setSelection(p_jarLocations.getProcessorSourceType() == processorSourceType);
        radio.addSelectionListener(new SimpleSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (radio.getSelection()) {
                    processorSelector.setProcessorSourceType(processorSourceType);
                }
            }
        });
    }

    /**
     * @param p_processorSelector the parent {@code ProcessorSelector}
     * @param processorSourceType the {@code ProcessorSourceType} this chooser is choosing for
     * @param radioLabel the label for the radio button in this chooser
     */
    protected ProcessorChooser(
        ProcessorSelector p_processorSelector,
        ProcessorSourceType processorSourceType,
        String radioLabel) {
        processorSelector = p_processorSelector;
        this.radioLabel = radioLabel;
        this.processorSourceType = processorSourceType;
    }

    protected IProject getProject() { return processorSelector.getProject(); }
    protected ProcessorSelector getProcessorSelector() { return processorSelector; }
    ProcessorSourceType getProcessorSourceType() { return processorSourceType; }
}