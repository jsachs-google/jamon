package org.jamon.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jamon.api.ParsedTemplate;
import org.jamon.api.ParserError;
import org.jamon.api.ParserErrors;
import org.jamon.api.SourceGenerator;
import org.jamon.api.TemplateParser;
import org.jamon.api.TemplateSource;

public class TemplateBuilder extends IncrementalProjectBuilder
{

    private TemplateDependencies m_dependencies = null;

    private IPath getWorkDir()
    {
        return getProject().getWorkingLocation(JamonProjectPlugin.getDefault().pluginId());
    }

    private File getDependencyFile()
    {
        return new File(getWorkDir().toOSString(), "dependencies");
    }

    private void loadDependencies()
    {
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(getDependencyFile());
            m_dependencies = new TemplateDependencies(in);
            in.close();
        }
        catch (FileNotFoundException e)
        {
            // ok, no cached dependencies
        }
        catch (IOException e)
        {
            EclipseUtils.logError(e);
        }
        finally
        {
            EclipseUtils.closeQuiety(in);
        }
    }

    private void saveDependencies()
    {
        try
        {
            FileOutputStream out = new FileOutputStream(getDependencyFile());
            m_dependencies.save(out);
            out.close();
        }
        catch (IOException e)
        {
            EclipseUtils.logError(e);
        }
    }

    @Override
    protected IProject[] build(int kind, @SuppressWarnings("unchecked") Map args, IProgressMonitor monitor) throws CoreException
    {
        if (kind == CLEAN_BUILD || kind == FULL_BUILD)
        {
            getProject().deleteMarkers(
                JamonProjectPlugin.getParentMarkerType(),
                true,
                IResource.DEPTH_INFINITE);
        }

        if (m_dependencies == null)
        {
            loadDependencies();
        }
        if (kind == IncrementalProjectBuilder.FULL_BUILD || m_dependencies == null)
        {
            fullBuild(monitor);
        }
        else
        {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null)
            {
                fullBuild(monitor);
            }
            else
            {
                incrementalBuild(delta, monitor);
            }
        }
        saveDependencies();
        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException
    {
        IFolder tsrc = getNature().getTemplateOutputFolder();
        for (IResource thing : tsrc.members())
        {
            EclipseUtils.unsetReadOnly(thing);
            thing.delete(true, monitor);
        }
     }

    private synchronized void fullBuild(IProgressMonitor monitor) throws CoreException
    {
        m_dependencies = new TemplateDependencies();
        getProject().accept(new BuildVisitor());
    }

    private JamonNature getNature() throws CoreException
    {
        return (JamonNature) getProject().getNature(JamonNature.natureId());
    }

    private synchronized void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException
    {
        BuildVisitor visitor = new BuildVisitor();
        delta.accept(visitor);
        @SuppressWarnings("unchecked") Set<IPath> changed = visitor.getChanged();
        IFolder templateDir = getNature().getTemplateSourceFolder();
        for (Iterator<IPath> i = changed.iterator(); i.hasNext(); )
        {
            IPath s = i.next();
            for (String dependency : m_dependencies.getDependenciesOf(s.toString()))
            {
                visitor.visit(templateDir.findMember(
                    new Path(dependency).addFileExtension(
                        JamonNature.JAMON_EXTENSION)));
            }
        }
    }

    private IJavaProject getJavaProject() throws CoreException
    {
        return (IJavaProject) (getProject().getNature(JavaCore.NATURE_ID));
    }

    private class BuildVisitor implements IResourceVisitor, IResourceDeltaVisitor
    {
        BuildVisitor() throws CoreException
        {
            m_templateDir = getNature().getTemplateSourceFolder();
            m_source = new ResourceTemplateSource(m_templateDir);
            ClassLoader classLoader = classLoader();
            try
            {
                m_templateParser = TemplateParser.class.cast(
                    classLoader
                        .loadClass("org.jamon.codegen.TemplateParserImpl")
                        .getConstructor(TemplateSource.class, ClassLoader.class)
                        .newInstance(m_source, classLoader));
            }
            catch (Exception e)
            {
                throw EclipseUtils.createCoreException(e);
            }
            m_outFolder = getNature().getTemplateOutputFolder();
            m_changed = new HashSet<IPath>();
        }

        Set<IPath> getChanged()
        {
            return m_changed;
        }

        private List<URL> classpathUrlsForProject(IJavaProject p_project)
            throws CoreException
        {
            List<URL> urls = new ArrayList<URL>();
            for (String entry : JavaRuntime.computeDefaultRuntimeClassPath(p_project))
            {
                try
                {
                    urls.add(new File(entry).toURL());
                }
                catch (MalformedURLException e)
                {
                    EclipseUtils.logError(e);
                }
            }
            addDependenciesClasspath(p_project, urls);
            return urls;
        }

        private void addDependencyClasspath(IProject p_project, List<URL> urls) throws CoreException
        {
            if (! p_project.isOpen())
            {
                EclipseUtils.logInfo("Apparently dependent project " + p_project.getName() + " not open, ignoring");
                return;
            }
            if (! p_project.hasNature(JavaCore.NATURE_ID))
            {
                EclipseUtils.logInfo("Dependent project " + p_project.getName() + " not a java project, ignoring");
                return;
            }
            urls.addAll(classpathUrlsForProject((IJavaProject) p_project.getNature(JavaCore.NATURE_ID)));
        }

        private void addDependenciesClasspath(IJavaProject p_project, List<URL> urls) throws CoreException
        {
            for (final IProject proj : p_project.getProject().getReferencedProjects())
            {
                addDependencyClasspath(proj, urls);
            }
        }

        private ClassLoader classLoader() throws CoreException
        {
            List<URL> urls = classpathUrlsForProject(getJavaProject());
            // TODO: does this have the proper parent?
            return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        }

        private final ResourceTemplateSource m_source;
        private final TemplateParser m_templateParser;
        private final IFolder m_templateDir;
        private final IFolder m_outFolder;
        private final Set<IPath> m_changed;

        private void createParents(IContainer p_container) throws CoreException
        {
            if (! p_container.exists())
            {
                createParents(p_container.getParent());
                ((IFolder) p_container).create(true, true, null);
                if (m_outFolder.equals(p_container))
                {
                  // If the template output folder doesn't exist (which might
                  // be the case, for example, if a project is checked out from
                  // source control), then Eclipse will mark this as a problem,
                  // and not realize that it has been fixed unless we
                  // explicitely revalidate the classpath.
                  getNature().revalidateClasspath();
                }
            }
        }

        private void markFile(ParserError e) throws CoreException
        {
            EclipseUtils.populateProblemMarker(
                ((ResourceTemplateLocation) e.getLocation().getTemplateLocation())
                    .getFile().createMarker(JamonProjectPlugin.getJamonMarkerType()),
                e.getLocation().getLine(),
                e.getMessage(), IMarker.SEVERITY_ERROR);
        }

        private void addMarkers(ParserErrors p_errors) throws CoreException
        {
            for (ParserError error: p_errors.getErrors())
            {
                markFile(error);
            }
        }

        private ParsedTemplate parse(IPath path, IFile file) throws CoreException
        {
            file.deleteMarkers(JamonProjectPlugin.getParentMarkerType(), true, IResource.DEPTH_ZERO);
            try
            {
                return m_templateParser.parseTemplate(
                    "/" + path.toString().replaceAll(File.separator, "/"));
            }
            catch (Exception e)
            {
                if (e instanceof ParserErrors) {
                    addMarkers((ParserErrors) e);
                    return null;
                }
                throw EclipseUtils.createCoreException(e);
            }
        }

        private byte[] generateSource(SourceGenerator sourceGenerator)
          throws CoreException
          {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                sourceGenerator.generateSource(baos);
                return baos.toByteArray();
            }
            catch (Exception e)
            {
                if (e instanceof ParserErrors) {
                    addMarkers((ParserErrors) e);
                    return null;
                }
                throw EclipseUtils.createCoreException(e);
            }
        }

        private TemplateResources makeResources(IResource p_resource)
        {
            if (p_resource.getType() == IResource.FILE)
            {
                IFile file = (IFile) p_resource;
                if (JamonNature.JAMON_EXTENSION.equals(file.getFileExtension())
                    && m_templateDir.getFullPath().isPrefixOf(file.getFullPath()))
                {
                    return new TemplateResources(file, m_outFolder, m_templateDir);
                }
            }
            return null;
        }

        private void createSourceFile(byte[] contents, IFile p_file) throws CoreException
        {
            if (contents != null)
            {
                createParents(p_file.getParent());
                p_file.create(new ByteArrayInputStream(contents), true, null);
            }
            else
            {
                if (p_file.exists())
                {
                    p_file.delete(true, false, null);
                }
            }
        }

        public boolean visit(IResource p_resource) throws CoreException
        {
            TemplateResources resources = makeResources(p_resource);
            if (resources != null)
            {
                resources.clearGeneratedResources();
                ParsedTemplate parsedTemplate = parse(resources.getPath(), resources.getTemplate());
                if (parsedTemplate != null)
                {
                    IPath path = resources.getPath().makeAbsolute();
                    m_dependencies.setCalledBy(
                        path.toString(), parsedTemplate.getTemplateDependencies());
                    m_changed.add(path);
                    createSourceFile(
                        generateSource(parsedTemplate.getProxyGenerator()),
                        resources.getProxy());
                    createSourceFile(
                        generateSource(parsedTemplate.getImplGenerator()),
                        resources.getImpl());
                }
            }
            return shouldDescendInto(p_resource);
        }

        private boolean shouldDescendInto(IResource p_resource) throws CoreException
        {
            if (p_resource.getType() == IResource.PROJECT)
            {
                return JamonNature.projectHasNature((IProject) p_resource);
            }
            if (p_resource.getType() == IResource.FILE) {
                return true;
            }
            if (p_resource.getType() != IResource.FOLDER)
            {
                return false;
            }
            final IPath fp = ((IFolder) p_resource).getProjectRelativePath();
            final IPath tp = m_templateDir.getProjectRelativePath();
            return fp.isPrefixOf(tp) || tp.isPrefixOf(fp);
        }

        public boolean visit(IResourceDelta p_delta) throws CoreException
        {
            switch(p_delta.getKind())
            {
                case IResourceDelta.ADDED :
                case IResourceDelta.CHANGED :
                    return visit(p_delta.getResource());
                case IResourceDelta.REMOVED:
                    TemplateResources resources = makeResources(p_delta.getResource());
                    if (resources != null)
                    {
                        resources.clearGeneratedResources();
                    }
            }
            return shouldDescendInto(p_delta.getResource());
        }
    }

    private static String builderId() {
        return JamonProjectPlugin.getDefault().pluginId() + ".templateBuilder";
    }

    public static void addToProject(IProject p_project) throws CoreException
    {
        IProjectDescription description = p_project.getDescription();
        List<ICommand> cmds = new ArrayList<ICommand>();
        cmds.addAll(Arrays.asList(description.getBuildSpec()));
        for (ICommand command : cmds)
        {
            if (command.getBuilderName().equals(builderId()))
            {
                return;
            }
        }
        ICommand jamonCmd = description.newCommand();
        jamonCmd.setBuilderName(builderId());
        cmds.add(0, jamonCmd);
        description.setBuildSpec(cmds.toArray(new ICommand[cmds.size()]));
        p_project.setDescription(description, null);
    }

}
