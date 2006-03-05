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
import org.jamon.JamonRuntimeException;
import org.jamon.codegen.Analyzer;
import org.jamon.codegen.ImplGenerator;
import org.jamon.codegen.ProxyGenerator;
import org.jamon.codegen.SourceGenerator;
import org.jamon.codegen.TemplateDescriber;
import org.jamon.codegen.TemplateUnit;
import org.jamon.ParserError;
import org.jamon.ParserErrors;
import org.jamon.util.StringUtils;


public class TemplateBuilder extends IncrementalProjectBuilder {

    private TemplateDependencies m_dependencies = null;
	private IPath getWorkDir() {
		return getProject().getWorkingLocation(JamonProjectPlugin.getDefault().pluginId());
	}

	private File getDependencyFile() {
		return new File(getWorkDir().toOSString(), "dependencies");
	}

	private void loadDependencies() {
		FileInputStream in;
		try {
			in = new FileInputStream(getDependencyFile());
			m_dependencies = new TemplateDependencies(in);
			in.close();
		}
		catch (FileNotFoundException e) {
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void saveDependencies() {
		try {
			FileOutputStream out = new FileOutputStream(getDependencyFile());
			m_dependencies.save(out);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
	    if (kind == CLEAN_BUILD || kind == FULL_BUILD)
        {
	        EclipseUtils.logInfo("deleting all jamon parent markers");
            getProject().deleteMarkers(
                JamonProjectPlugin.getParentMarkerType(),
                true,
                IResource.DEPTH_INFINITE);
        }

		if (m_dependencies == null) {
			loadDependencies();
		}
		if (kind == IncrementalProjectBuilder.FULL_BUILD || m_dependencies == null) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			}
			else {
				incrementalBuild(delta, monitor);
			}
		}
		saveDependencies();
		return null;
	}

	@Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
		IFolder tsrc = getNature().getTemplateOutputFolder();
		for (IResource thing : tsrc.members())
        {
            EclipseUtils.unsetReadOnly(thing);
			thing.delete(true, monitor);
		}
     }

	private synchronized void fullBuild(IProgressMonitor monitor) throws CoreException {
		m_dependencies = new TemplateDependencies();
		getProject().accept(new BuildVisitor());
	}

	private JamonNature getNature() throws CoreException {
		return (JamonNature) getProject().getNature(JamonNature.natureId());
	}

	private synchronized void incrementalBuild(
        IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
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

	private IJavaProject getJavaProject() throws CoreException {
		return (IJavaProject) (getProject().getNature(JavaCore.NATURE_ID));
	}

	private class BuildVisitor implements IResourceVisitor, IResourceDeltaVisitor {
		BuildVisitor() throws CoreException  {
			m_templateDir = getNature().getTemplateSourceFolder();
			m_source = new ResourceTemplateSource(m_templateDir);
            m_describer = new TemplateDescriber(m_source, classLoader());
			m_outFolder = getNature().getTemplateOutputFolder();
			m_changed = new HashSet<IPath>();
		}

		Set getChanged() {
			return m_changed;
		}

		private List<URL> classpathUrlsForProject(IJavaProject p_project)
            throws CoreException
        {
			List<URL> urls = new ArrayList<URL>();
			String[] entries = JavaRuntime.computeDefaultRuntimeClassPath(p_project);
			for (int i = 0; i < entries.length; ++i) {
				try {
					urls.add(new File(entries[i]).toURL());
				}
				catch (MalformedURLException e) {
					EclipseUtils.logError(e);
				}
			}
			IProject[] dependencies = p_project.getProject().getReferencedProjects();
			for (int i = 0; i < dependencies.length; ++i) {
				if (dependencies[i].hasNature(JavaCore.NATURE_ID)) {
					urls.addAll(classpathUrlsForProject((IJavaProject) dependencies[i].getNature(JavaCore.NATURE_ID)));
				}
			}
			return urls;
		}

		private ClassLoader classLoader() throws CoreException {
			List<URL> urls = classpathUrlsForProject(getJavaProject());
			// TODO: does this have the proper parent?
			return new URLClassLoader(urls.toArray(new URL[urls.size()]));

		}

		private final ResourceTemplateSource m_source;
		private final TemplateDescriber m_describer;
		private final IFolder m_templateDir;
		private final IFolder m_outFolder;
		private final Set<IPath> m_changed;

		private void createParents(IContainer p_container) throws CoreException {
			if (! p_container.exists()) {
				createParents(p_container.getParent());
				((IFolder) p_container).create(true, true, null);
                if (getNature().getTemplateOutputFolder().equals(p_container)) {
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
            for (Iterator<ParserError> i = p_errors.getErrors(); i.hasNext(); )
            {
                markFile(i.next());
            }
        }

		private TemplateUnit analyze(IPath path, IFile file) throws CoreException {
			file.deleteMarkers(JamonProjectPlugin.getParentMarkerType(), true, IResource.DEPTH_ZERO);
			try {
				return new Analyzer(
                    "/" + StringUtils.filePathToTemplatePath(path.toString()),
                    m_describer)
                    .analyze();
			}
            catch (ParserErrors e) {
                addMarkers(e);
                return null;
            }
			catch (IOException e) {
				throw EclipseUtils.createCoreException(e);
			}
			catch (JamonRuntimeException e) {
				throw EclipseUtils.createCoreException(e);
			}
		}

		private byte[] generateSource(
          TemplateUnit templateUnit, IFile file, SourceGenerator sourceGenerator)
          throws CoreException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
                sourceGenerator.generateSource(baos);
                return baos.toByteArray();
			}
            catch (ParserErrors e) {
                addMarkers(e);
                return null;
            }
            catch (IOException e) {
				throw EclipseUtils.createCoreException(e);
			}
			catch (JamonRuntimeException e) {
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
                TemplateUnit templateUnit = analyze(resources.getPath(), resources.getTemplate());
                if (templateUnit != null)
                {
                    IPath path = resources.getPath().makeAbsolute();
                    m_dependencies.setCalledBy(
                        path.toString(), templateUnit.getTemplateDependencies());
                    m_changed.add(path);
                    createSourceFile(
                        generateSource(templateUnit,
                                       resources.getTemplate(),
                                       new ProxyGenerator(m_describer, templateUnit)),
                        resources.getProxy());
                    createSourceFile(
                        generateSource(templateUnit,
                                       resources.getTemplate(),
                                       new ImplGenerator(m_describer, templateUnit)),
                        resources.getImpl());
                }
            }
            return true;
		}

		public boolean visit(IResourceDelta p_delta) throws CoreException
        {
            switch(p_delta.getKind())
            {
                case IResourceDelta.ADDED :
                case IResourceDelta.CHANGED :
                    visit(p_delta.getResource());
                break;
                case IResourceDelta.REMOVED:
                    TemplateResources resources =
                        makeResources(p_delta.getResource());
                    if (resources != null)
                    {
                        resources.clearGeneratedResources();
                    }
            }
            return true;
        }
	}

	private static String builderId() {
		return JamonProjectPlugin.getDefault().pluginId() + ".templateBuilder";
	}

	public static void addToProject(IProject p_project) throws CoreException {
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