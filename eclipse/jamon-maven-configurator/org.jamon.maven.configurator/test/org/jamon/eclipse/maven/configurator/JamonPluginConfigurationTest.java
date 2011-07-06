package org.jamon.eclipse.maven.configurator;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.mockito.Mockito;

public class JamonPluginConfigurationTest {

  @Test
  public void testGetProjectRelativePathWhenInProject() {
    assertEquals(
      "resource",
      JamonPluginConfiguration.getProjectRelativePath(
        "/project/resource", projectAtLocation("/project")));
  }

  @Test
  public void testGetProjectRelativePathWhenNotInProject() {
    assertEquals(
      "/other/resource",
      JamonPluginConfiguration.getProjectRelativePath(
        "/other/resource", projectAtLocation("/project")));
  }

  private IProject projectAtLocation(String projectPath) {
    IProject project = Mockito.mock(IProject.class);
    Mockito.when(project.getLocation()).thenReturn(new Path(projectPath));
    return project;
  }

}
