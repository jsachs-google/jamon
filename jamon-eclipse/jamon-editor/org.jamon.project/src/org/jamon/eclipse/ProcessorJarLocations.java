/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class ProcessorJarLocations
{
    private static final String PROCESSOR_JAR_SOURCE_TYPE = "processJarSourceType";
    private static final String PROCESSOR_JAR_WORKSPACE_PATH= "processJarWorkspacePath";
    private static final String PROCESSOR_JAR_EXTERNAL_PATH = "processJarExternalPath";
    private static final String PROCESSOR_JAR_VARIABLE_PATH = "processJarVariablePath";

    public ProcessorJarLocations() {}
    public ProcessorJarLocations(IEclipsePreferences p_preferences) {
        withProcessorSourceType(
            ProcessorSourceType.fromPreferenceValue(
                p_preferences.get(PROCESSOR_JAR_SOURCE_TYPE, null)));
        withWorkspaceLocation(
            stringToPath(p_preferences.get(PROCESSOR_JAR_WORKSPACE_PATH, null)));
        withExternalLocation(
            stringToPath(p_preferences.get(PROCESSOR_JAR_EXTERNAL_PATH, null)));
        withVariableLocation(
            stringToPath(p_preferences.get(PROCESSOR_JAR_VARIABLE_PATH, null)));
    }

    public void storePrefs(IEclipsePreferences p_projectNode)
    {
        p_projectNode.put(PROCESSOR_JAR_SOURCE_TYPE, m_processorSourceType.preferenceValue());
        p_projectNode.put(PROCESSOR_JAR_WORKSPACE_PATH, pathToString(m_workspaceLocation));
        p_projectNode.put(PROCESSOR_JAR_EXTERNAL_PATH, pathToString(m_externalLocation));
        p_projectNode.put(PROCESSOR_JAR_VARIABLE_PATH, pathToString(m_variableLocation));
    }

    public static File getPluginProcessorJar()
    {
        Bundle processorPluginBundle = Platform.getBundle("org.jamon.processor");
        Enumeration<URL> matches = 
            processorPluginBundle.findEntries("/", "jamon-processor*.jar", false);
        if (matches != null)
        {
            while(matches.hasMoreElements())
            {
                URL match = matches.nextElement();
                try
                {
                    return new File(
                        FileLocator.toFileURL(processorPluginBundle.getEntry(match.getPath())).toURI());
                }
                catch (Exception e)
                {
                    EclipseUtils.logError(e);
                    return null;
                }
            }
        }
        return null;
    }

    public File jarFile(IProject p_project) {
        switch (m_processorSourceType) {
        case PLUGIN: return getPluginProcessorJar();
        case WORKSPACE: return workspacePathToFile(p_project, m_workspaceLocation);
        case EXTERNAL: return externalPathToFile(m_externalLocation);
        case VARIABLE: return variablePathToFile(m_variableLocation);
        }
        throw new IllegalStateException("unknown processor source type: " + m_processorSourceType);
    }

    public static File workspacePathToFile(IProject p_project, IPath p_path) {
        return new File(
            p_project.getWorkspace().getRoot().getLocation().append(p_path).toOSString());
    }

    public static File externalPathToFile(IPath p_path) {
        return new File(p_path.toOSString());
    }

    public static File variablePathToFile(IPath p_path) {
        return new File(JavaCore.getResolvedVariablePath(p_path).toOSString());
    }

    public IPath getLocationForSourceType(ProcessorSourceType p_processorSourceType) {
        switch (p_processorSourceType)
        {
        case PLUGIN:
            throw new IllegalArgumentException("cannot get location for type PLUGIN");
        case WORKSPACE: return m_workspaceLocation;
        case EXTERNAL: return m_externalLocation;
        case VARIABLE: return m_variableLocation;
        }
        throw new IllegalStateException("unexpected ProcessorSourceType " + p_processorSourceType);
    }

    public ProcessorJarLocations withWorkspaceLocation(IPath p_workspaceLocation)
    {
        m_workspaceLocation = p_workspaceLocation;
        return this;
    }

    public ProcessorJarLocations withExternalLocation(IPath p_externalLocation)
    {
        m_externalLocation = p_externalLocation;
        return this;
    }

    public ProcessorJarLocations withVariableLocation(IPath p_variableLocation)
    {
        m_variableLocation = p_variableLocation;
        return this;
    }

    public ProcessorSourceType getProcessorSourceType() { return m_processorSourceType; }
    public ProcessorJarLocations withProcessorSourceType(ProcessorSourceType p_processorSourceType)
    {
        m_processorSourceType = p_processorSourceType;
        return this;
    }

    private static IPath stringToPath(String p_pathString) {
        return (p_pathString == null || p_pathString.length() == 0)
            ? null
            : Path.fromPortableString(p_pathString);
    }

    private static String pathToString(IPath p_path) {
        return p_path == null ? "" : p_path.toPortableString();
    }

    private IPath m_workspaceLocation, m_externalLocation, m_variableLocation;
    private ProcessorSourceType m_processorSourceType;
}
