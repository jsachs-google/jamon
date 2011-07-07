package org.jamon.eclipse.maven.configurator;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;

public class JamonPluginConfiguration {
  private static final String JAMON_PLUGIN_ARTIFACT_ID = "jamon-maven-plugin";

  private static final String JAMON_GROUP_ID = "org.jamon";

  private final String templateOutputDir;
  private final String templateSourceDir;

  public static JamonPluginConfiguration forProjectConfigurationRequest(
    ProjectConfigurationRequest request) {
    Plugin jamonPlugin = getJamonPlugin(request.getMavenProject());
    if (jamonPlugin == null) {
      return null;
    }
    Xpp3Dom pluginConfiguration = getPluginConfiguration(jamonPlugin);
    if (pluginConfiguration == null) {
      return null;
    }
    IProject project = request.getProject();
    return new JamonPluginConfiguration(
      getConfigurationPathValue(pluginConfiguration, "templateOutputDir", "tsrc", project),
      getConfigurationPathValue(pluginConfiguration, "templateSourceDir", "src/templates", project));
  }

  private JamonPluginConfiguration(String templateOutputDir, String templateSourceDir) {
    this.templateOutputDir = templateOutputDir;
    this.templateSourceDir = templateSourceDir;
  }

  public String getTemplateOutputDir() {
    return templateOutputDir;
  }

  public String getTemplateSourceDir() {
    return templateSourceDir;
  }

  /**
   * Gets the Jamon plugin from pom.xml. Returns null if it is not found.
   *
   * @param project
   * @return
   */
  private static Plugin getJamonPlugin(MavenProject project) {
    List<Plugin> plugins = project.getBuildPlugins();

    for (Plugin plugin : plugins) {
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
   * Get the jamon plugin configuration section. If it is not found in the main configuration
   * section it then looks in the configuration section for the generate-sources execution.
   *
   * @param plugin
   * @return configuration - Returns null if not found
   */
  private static Xpp3Dom getPluginConfiguration(Plugin plugin) {
    //TODO - property interpolation
    Object configuration = plugin.getConfiguration();
    if (configuration == null) {
      if (plugin.getExecutions() != null) {
        configuration = getConfigurationFromGenerateSourcesExecution(plugin);
      }
    }
    if (configuration instanceof Xpp3Dom) {
      return (Xpp3Dom) configuration;
    }
    else {
      return null;
    }
  }

  /**
   * Gets the configuration section from the generate sources execution. Returns null if it is not
   * found.
   *
   * @param plugin
   * @return
   */
  private static Object getConfigurationFromGenerateSourcesExecution(Plugin plugin) {
    for (PluginExecution execution : plugin.getExecutions()) {
      // Assume that we use the configuration that is executed in generate-sources
      if ("generate-sources".equals(execution.getPhase())) {
        return execution.getConfiguration();
      }
    }
    return null;
  }

  /**
   * Gets a value from the configuration sections. Returns a defaultValue if none is found.
   *
   * @param configuration
   * @param key
   * @param defaultValue
   * @return
   */
  private static String getConfigurationPathValue(
    Xpp3Dom configuration, String key, String defaultValue, IProject project) {
    String value = getConfigurationPathValue(configuration, key);
    return getProjectRelativePath(StringUtils.isBlank(value) ? defaultValue : value, project);
  }

  /**
   * Gets a value from the configuration section. Returns null if it is not found.
   *
   * @param configuration
   * @param key
   * @return
   */
  private static String getConfigurationPathValue(Xpp3Dom configuration, String key) {
    Xpp3Dom configEntry = configuration.getChild(key);
    return configEntry == null ? null : configEntry.getValue();
  }

  /**
   * Gets a path relative to the project
   *
   * @param path
   * @param project
   * @return
   */
  static String getProjectRelativePath(String path, IProject project) {
    IPath projectLocation = project.getLocation();
    String projectLocationString = projectLocation.toOSString() + File.separator;
    if (path.startsWith(projectLocationString)) {
      return path.substring(projectLocationString.length());
    }
    else {
      return path;
    }
  }
}
