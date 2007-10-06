package org.jamon.eclipse;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

public class JamonNature implements IProjectNature {

    static String natureId() {
        return JamonProjectPlugin.getDefault().pluginId() + ".jamonnature";
    }

    public static boolean projectHasNature(IProject p_project) throws CoreException {
        return p_project.getProject().hasNature(natureId());
    }

    private static List<String> naturesList(IProjectDescription p_desc) {
        return new ArrayList<String>(Arrays.asList(p_desc.getNatureIds()));
    }

    private static void setNatures(
        IProjectDescription p_description, List<String> p_natures)
    {
        p_description.setNatureIds(p_natures.toArray(new String[p_natures.size()]));
    }

    public static void addToProject(
        IProject p_project, String p_templateSourceDir, String p_templateOutputDir,
        ProcessorSourceType processorSourceType,
        IPath workspaceJar, IPath externalJar, IPath variableJar)
    throws CoreException {
        IProjectDescription description = p_project.getDescription();
        List<String> natures = naturesList(description);
        if (! natures.contains(natureId())) {
            natures.add(natureId());
            setNatures(description, natures);
        }
        IEclipsePreferences projectNode = preferences(p_project);
        projectNode.put(TEMPLATE_SOURCE_DIR_PROPERTY, p_templateSourceDir);
        projectNode.put(TEMPLATE_OUTPUT_DIR_PROPERTY, p_templateOutputDir);
        projectNode.put(PROCESSOR_JAR_SOURCE_TYPE, processorSourceType.preferenceValue());
        projectNode.put(PROCESSOR_JAR_WORKSPACE_PATH, pathToString(workspaceJar));
        projectNode.put(PROCESSOR_JAR_EXTERNAL_PATH, pathToString(externalJar));
        projectNode.put(PROCESSOR_JAR_VARIABLE_PATH, pathToString(variableJar));
        try {
            projectNode.flush();
        }
        catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        p_project.setDescription(description, null);
    }

    private static IEclipsePreferences preferences(IProject p_project) {
        IScopeContext projectScope = new ProjectScope(p_project);
        IEclipsePreferences node = projectScope.getNode(JAMON_PREFERENCES_NODE);
        if (node == null) {
            throw new RuntimeException("Couldn't find jamon preferences node");
        }
        return node;
    }

    private static final String TEMPLATE_SOURCE_DIR_PROPERTY = "templateSourceDir";
    private static final String TEMPLATE_OUTPUT_DIR_PROPERTY = "templateOutputDir";
    private static final String PROCESSOR_JAR_SOURCE_TYPE = "processJarSourceType";
    private static final String PROCESSOR_JAR_WORKSPACE_PATH= "processJarWorkspacePath";
    private static final String PROCESSOR_JAR_EXTERNAL_PATH = "processJarExternalPath";
    private static final String PROCESSOR_JAR_VARIABLE_PATH = "processJarVariablePath";

    private static final String JAMON_PREFERENCES_NODE = "org.jamon";

    public static void removeFromProject(IProject p_project) throws CoreException
    {
        IProjectDescription description = p_project.getDescription();
        List<String> natures = naturesList(description);
        if (natures.contains(natureId())) {
            natures.remove(natureId());
            setNatures(description, natures);
        }
        p_project.setDescription(description, null);
    }

    static IFolder templateOutputFolder(IProject p_project)
    {
        return p_project.getFolder(new Path(templateOutputFolderName(p_project)));
    }

    public IFolder getTemplateOutputFolder() {
        return templateOutputFolder(getProject());
    }

    static String templateSourceFolderName(IProject p_project) {
        return preferences(p_project)
            .get(TEMPLATE_SOURCE_DIR_PROPERTY, DEFAULT_TEMPLATE_SOURCE);
    }

    static String templateOutputFolderName(IProject p_project) {
        return preferences(p_project)
            .get(TEMPLATE_OUTPUT_DIR_PROPERTY, DEFAULT_OUTPUT_DIR);
    }

    static IFolder templateSourceFolder(IProject p_project)
    {
        return p_project.getFolder(
            new Path(templateSourceFolderName(p_project)));
    }

    static ProcessorSourceType processorSourceType(IProject p_project) {
        return ProcessorSourceType.fromPreferenceValue(
            preferences(p_project).get(PROCESSOR_JAR_SOURCE_TYPE, null));
    }

    private static IPath stringToPath(String pathString) {
        return (pathString == null || pathString.length() == 0)
            ? null
            : Path.fromPortableString(pathString);
    }

    private static String pathToString(IPath path) {
        return path == null ? "" : path.toPortableString();
    }

    static IPath workspaceJar(IProject p_project) {
        return stringToPath(preferences(p_project).get(PROCESSOR_JAR_WORKSPACE_PATH, null));
    }

    static IPath externalJar(IProject p_project) {
        return stringToPath(preferences(p_project).get(PROCESSOR_JAR_EXTERNAL_PATH, null));
    }

    static IPath variableJar(IProject p_project) {
        return stringToPath(preferences(p_project).get(PROCESSOR_JAR_VARIABLE_PATH, null));
    }

    static File jarFile(IProject p_project) {
        switch (processorSourceType(p_project)) {
        case PLUGIN: return getPluginProcessorJar();
        case WORKSPACE: return workspacePathToFile(p_project, workspaceJar(p_project));
        case EXTERNAL: return externalPathToFile(externalJar(p_project));
        case VARIABLE: return variablePathToFile(variableJar(p_project));
        }
        throw new IllegalStateException(
            "unknown processor source type: " + processorSourceType(p_project));
    }

    static File workspacePathToFile(IProject p_project, IPath p_path) {
        return new File(
            p_project.getWorkspace().getRoot().getLocation().append(p_path).toOSString());
    }

    static File externalPathToFile(IPath p_path) {
        return new File(p_path.toOSString());
    }

    static File variablePathToFile(IPath p_path) {
        return new File(JavaCore.getResolvedVariablePath(p_path).toOSString());
    }

    static File getPluginProcessorJar()
    {
        Bundle processorPluginBundle = Platform.getBundle("org.jamon.processor");
        for (
                @SuppressWarnings("unchecked") Enumeration<URL> matches =
                    processorPluginBundle.findEntries("/", "jamon-processor*.jar", false);
                matches.hasMoreElements(); )
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
        return null;
    }

    public IFolder getTemplateSourceFolder() {
        return templateSourceFolder(getProject());
    }

    private void unsetReadOnly(IContainer p_container) throws CoreException {
        IResource[] members = p_container.members();
        for (IResource resource : members)
        {
            EclipseUtils.unsetReadOnly(resource);
            if (resource instanceof IContainer) {
                unsetReadOnly((IContainer) resource);
            }
        }
    }

    private void removeTsrc() throws CoreException
    {
        IFolder tsrc = getTemplateOutputFolder();
        IJavaProject jp = getJavaProject();
        List<IClasspathEntry> e =
            new ArrayList<IClasspathEntry>(Arrays.asList(jp.getRawClasspath()));
        e.remove(JavaCore.newSourceEntry(tsrc.getFullPath()));
        jp.setRawClasspath(e.toArray(new IClasspathEntry[e.size()]), null);
        if (tsrc.exists())
        {
            unsetReadOnly(tsrc);
        }
    }

    public void configure() throws CoreException
    {
        TemplateBuilder.addToProject(getProject());
        MarkerUpdaterBuilder.addToProject(getProject());
        removeTsrc();
        IFolder tsrc = getTemplateOutputFolder();
        if (! tsrc.exists())
        {
            tsrc.create(true, true, null);
            tsrc.setDerived(true);
        }
        IJavaProject jp = getJavaProject();
        List<IClasspathEntry> e =
            new ArrayList<IClasspathEntry>(Arrays.asList(jp.getRawClasspath()));
        e.add(JavaCore.newSourceEntry(tsrc.getFullPath()));
        jp.setRawClasspath(e.toArray(new IClasspathEntry[e.size()]), null);
    }

    public void revalidateClasspath() throws CoreException {
      IJavaProject jp = getJavaProject();
      jp.setRawClasspath(jp.getRawClasspath(), null);
    }

    private IJavaProject getJavaProject() throws CoreException {
        return (IJavaProject) (getProject().getNature(JavaCore.NATURE_ID));
    }

    public void deconfigure() throws CoreException {
        removeTsrc();
    }

    public IProject getProject() {
        return m_project;
    }

    public void setProject(IProject project) {
        m_project = project;
    }

    private IProject m_project;
    static final String JAMON_EXTENSION = "jamon";
    static final String DEFAULT_TEMPLATE_SOURCE = "templates";
    static final String DEFAULT_OUTPUT_DIR = "tsrc";

}
