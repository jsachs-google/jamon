/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released July, 2011.
 *
 * The Initial Developer of the Original Code is Scott Olcott.  Portions
 * created by Scott Olcott are Copyright (C) 2011 Scott Olcott.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon.eclipse.maven.configurator;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

public class JamonPluginConfiguration {
  private final String templateOutputDir;
  private final String templateSourceDir;
  private final Artifact jamonProcessorArtifact;

  public static JamonPluginConfiguration forProjectConfigurationRequest(
    ProjectConfigurationRequest request, IProgressMonitor monitor, IMaven maven)
  throws CoreException {
    IMavenProjectFacade mavenProjectFacade = request.getMavenProjectFacade();

    for (MojoExecution execution: mavenProjectFacade.getMojoExecutions(
      "org.jamon", "jamon-maven-plugin",monitor, "translate", "translate-tests")) {
      PluginDescriptor pluginDescriptor = execution.getMojoDescriptor().getPluginDescriptor();

      Object mojo = maven.getConfiguredMojo(request.getMavenSession(), execution, Object.class);
      Artifact jamonProcessorArtifact = null;
      for (Artifact artifact : pluginDescriptor.getArtifacts()) {
        if ("org.jamon".equals(artifact.getGroupId())
            && "jamon-processor".equals(artifact.getArtifactId())) {
          jamonProcessorArtifact = artifact;
          break;
        }
      }

      IProject project = request.getProject();
      return new JamonPluginConfiguration(
        getProjectRelativePath(getFileProperty(mojo, "templateSourceDir"), project),
        getProjectRelativePath(getFileProperty(mojo, "templateOutputDir"), project),
        jamonProcessorArtifact);

    }
    return null;
  }

  private static File getFileProperty(Object mojo, String propertyName) throws CoreException {
    try {
      Method getTemplateSourceDir = mojo.getClass().getMethod(getter(propertyName));
      return (File) getTemplateSourceDir.invoke(mojo);
    }
    catch (Exception e1) {
      try {
        Field field = mojo.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        return (File) field.get(mojo);
      }
      catch (Exception e2) {
        throw new CoreException(new Status(
          IStatus.ERROR, JamonProjectConfigurator.PLUGIN_ID, "failure to access " + propertyName, e2));
      }
    }

  }

  private JamonPluginConfiguration(
    String templateSourceDir, String templateOutputDir, Artifact jamonProcessorArtifact) {
    this.templateSourceDir = templateSourceDir;
    this.templateOutputDir = templateOutputDir;
    this.jamonProcessorArtifact = jamonProcessorArtifact;
  }

  public String getTemplateOutputDir() {
    return templateOutputDir;
  }

  public String getTemplateSourceDir() {
    return templateSourceDir;
  }

  public Artifact getJamonProcessorArtifact() {
    return jamonProcessorArtifact;
  }

  /**
   * Gets a path relative to the project
   *
   * @param file
   * @param project
   * @return
   */
  private static String getProjectRelativePath(File file, IProject project) {
    String path = file.getAbsolutePath();
    IPath projectLocation = project.getLocation();
    String projectLocationString = projectLocation.toOSString() + File.separator;
    if (path.startsWith(projectLocationString)) {
      return path.substring(projectLocationString.length());
    }
    else {
      return path;
    }
  }

  private static String getter(String property)
  {
    StringBuilder builder = new StringBuilder("get");
    builder.append(Character.toUpperCase(property.charAt(0)));
    builder.append(property.substring(1));
    return builder.toString();
  }

}
