/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 *
 */
package org.jamon.eclipse.projectprefspage;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jamon.eclipse.JamonNature;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;

public class ProcessorSelector {
    public ProcessorSelector(JamonProjectPropertyPage p_propertyPage) {
        propertyPage = p_propertyPage;
        addProcessorChooser(pluginProcessorChooser);
        addProcessorChooser(workspaceProcessorJarChooser);
        addProcessorChooser(externalProcessorJarChooser);
        addProcessorChooser(variableProcessorJarChooser);
    }

    private void addProcessorChooser(ProcessorChooser p_processorChooser) {
        processorChoosers.put(p_processorChooser.getProcessorSourceType(), p_processorChooser);
    }

    Map<ProcessorSourceType, ProcessorChooser> processorChoosers =
        new EnumMap<ProcessorSourceType, ProcessorChooser>(ProcessorSourceType.class);

    void setProcessorSourceType(ProcessorSourceType processorSourceType) {
        this.processorSourceType = processorSourceType;
        for (ProcessorSourceType option: ProcessorSourceType.values()) {
            processorChoosers.get(option).setEnabled(option == processorSourceType);
        }
    }

    void setEnabled(boolean p_enabled) {
        for (ProcessorChooser chooser: processorChoosers.values()) {
            chooser.setWidgetEnabled(p_enabled);
        }
        if (p_enabled && getProcessorSourceType() != null) {
            processorChoosers.get(getProcessorSourceType()).setEnabled(true);
        }
    }

    public ProcessorSourceType getProcessorSourceType() {
        return processorSourceType;
    }

    private final PluginProcessorChooser pluginProcessorChooser = new PluginProcessorChooser(this);
    private final WorkspaceProcessorJarChooser workspaceProcessorJarChooser =
        new WorkspaceProcessorJarChooser(this);
    private final ExternalProcessorJarChooser externalProcessorJarChooser =
        new ExternalProcessorJarChooser(this);
    private final VariableProcessorJarChooser variableProcessorJarChooser =
        new VariableProcessorJarChooser(this);

    private final JamonProjectPropertyPage propertyPage;
    private ProcessorSourceType processorSourceType;

    public IProject getProject() {
        return propertyPage.getProject();
    }

    public void setJarIsValid(boolean jarIsValid) {
        propertyPage.setJarIsValid(jarIsValid);
    }

    public void render(Composite parent) {
        final Composite jarChoiceGroup = new Composite(parent, SWT.NONE);
        jarChoiceGroup.setLayout(new GridLayout(3, false));
        jarChoiceGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

        ProcessorJarLocations jarLocations =
            JamonNature.getProcessorJarLocations(getProject());

        pluginProcessorChooser.render(jarChoiceGroup, jarLocations);
        workspaceProcessorJarChooser.render(jarChoiceGroup, jarLocations);
        externalProcessorJarChooser.render(jarChoiceGroup, jarLocations);
        variableProcessorJarChooser.render(jarChoiceGroup, jarLocations);
        setProcessorSourceType(jarLocations.getProcessorSourceType());
    }

    public ProcessorJarLocations getProcessorJarLocations() {
        return new ProcessorJarLocations()
            .withProcessorSourceType(processorSourceType)
            .withWorkspaceLocation(workspaceProcessorJarChooser.getJarLocation())
            .withExternalLocation(externalProcessorJarChooser.getJarLocation())
            .withVariableLocation(variableProcessorJarChooser.getJarLocation());
    }
}