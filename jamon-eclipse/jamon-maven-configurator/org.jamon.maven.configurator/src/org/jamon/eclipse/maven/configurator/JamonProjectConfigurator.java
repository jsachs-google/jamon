package org.jamon.eclipse.maven.configurator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.jamon.eclipse.JamonNature;
import org.jamon.eclipse.ProcessorJarLocations;
import org.jamon.eclipse.ProcessorSourceType;
import org.maven.ide.eclipse.jdt.IClasspathDescriptor;
import org.maven.ide.eclipse.jdt.IJavaProjectConfigurator;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

public class JamonProjectConfigurator extends AbstractProjectConfigurator implements
    IJavaProjectConfigurator {

  private static final String JAMON_PLUGIN_ARTIFACT_ID = "jamon-maven-plugin";

  private static final String JAMON_PROCESSOR_ARTIFACT_ID = "jamon-processor";

  private static final String JAMON_GROUP_ID = "org.jamon";

  @Override
  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor)
  throws CoreException {
    JamonPluginConfiguration jamonPluginConfiguration =
      JamonPluginConfiguration.forProjectConfigurationRequest(request);
    if (jamonPluginConfiguration == null) {
      // Do nothing if there is no plugin configuration
      return;
    }
    Plugin jamonPlugin = getJamonPlugin(request.getMavenProject());
    if (jamonPlugin == null) {
      // Do nothing if the jamon maven plugin isn't defined
      return;
    }

    String templateSourceDir = jamonPluginConfiguration.getTemplateSourceDir();
    String templateOutputDir = jamonPluginConfiguration.getTemplateOutputDir();
    request.getProject().getFolder(".settings").refreshLocal(IResource.DEPTH_ONE, monitor);

    Artifact jamonProcessorArtifact = getJamonProcessorArtifact(request.getMavenProject());
    if (jamonProcessorArtifact != null) {
      JamonNature.addToProject(request.getProject(), templateSourceDir, templateOutputDir,
        new ProcessorJarLocations()
        .withProcessorSourceType(ProcessorSourceType.EXTERNAL)
        .withExternalLocation(
          Path.fromOSString(jamonProcessorArtifact.getFile().getAbsolutePath())));
    }
    else {
      JamonNature.addToProject(request.getProject(), templateSourceDir, templateOutputDir,
        new ProcessorJarLocations().withProcessorSourceType(ProcessorSourceType.PLUGIN));
    }

  }

  @Override
  public void unconfigure(ProjectConfigurationRequest request, IProgressMonitor monitor)
  throws CoreException {
    JamonNature.removeFromProject(request.getProject());
    super.unconfigure(request, monitor);
  }

  /**
   * This configures Maven Dependency Container classpath {@inheritDoc}
   */
  public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath,
    IProgressMonitor monitor) {
    // Nothing to do unless we are modifying the Maven Dependency Classpath
  }

  /**
   * This configures Eclipse's classpath. This adds templateOutputDir to the classpath
   */
  public void configureRawClasspath(ProjectConfigurationRequest request,
    IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {
    if (JamonNature.projectHasNature(request.getProject())) {
      JamonPluginConfiguration jamonPluginConfiguration =
        JamonPluginConfiguration.forProjectConfigurationRequest(request);
      if (jamonPluginConfiguration != null) {
        IFolder templateDir = request.getProject().getFolder(
          jamonPluginConfiguration.getTemplateOutputDir());
        createFolder(request.getProject(), templateDir, monitor);
        classpath.addEntry(JavaCore.newSourceEntry(templateDir.getFullPath().makeAbsolute()));
      }
    }
  }

  /**
   * Gets the Jamon plugin from pom.xml. Returns null if it is not found.
   *
   * @param project
   * @return
   */
  private static Plugin getJamonPlugin(MavenProject project) {
    for (Plugin plugin : project.getBuildPlugins()) {
      if (isJamonArtifact(plugin)) {
        return plugin;
      }
    }
    return null;
  }

  private static boolean isJamonArtifact(Plugin plugin) {
    return JAMON_GROUP_ID.equals(plugin.getGroupId())
      && JAMON_PLUGIN_ARTIFACT_ID.equals(plugin.getArtifactId());
  }

  /**
   * Creates a folder and any parent folders that don't exist inside of a project
   *
   * @param project
   * @param folder
   * @param monitor
   * @throws CoreException
   */
  private void createFolder(IProject project, IFolder folder, IProgressMonitor monitor)
  throws CoreException {
    if (!folder.exists()) {
      if (!folder.getParent().exists()) {
        createFolder(project, folder.getParent().getFolder(null), monitor);
      }
      folder.create(false, true, monitor);
    }
  }

  /**
   * Get the jamon processor artifact from project dependendies. Return null if it is not found
   *
   * @param mavenProject
   * @return artifact
   */
  private Artifact getJamonProcessorArtifact(MavenProject mavenProject) {
    //FIXME - this isn't the right thing to do; the processor generally is not a dependency of
    // the project, but of the jamon maven plugin.
    if (mavenProject != null && mavenProject.getArtifacts() != null) {
      for (Artifact artifact : mavenProject.getArtifacts()) {
        if (JAMON_GROUP_ID.equals(artifact.getGroupId())
          && JAMON_PROCESSOR_ARTIFACT_ID.equals(artifact.getArtifactId())) {
          return artifact;
        }
      }
    }
    return null;
  }

}
