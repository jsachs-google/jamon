package org.jamon.eclipse.maven.configurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.jamon.eclipse.JamonNature;
import org.junit.Test;

@SuppressWarnings("restriction")
public class JamonProjectConfiguratorTest extends AbstractMavenProjectTestCase {

  @Test
  public void test() throws Exception {
    IProject project = importAndBuildProject("basic-jamon-project");
    assertTrue(JamonNature.projectHasNature(project));
    assertEquals("tsrc", JamonNature.templateSourceFolderName(project));
    assertEquals("foo", JamonNature.templateOutputFolderName(project));
  }

  private IProject importAndBuildProject(String name) throws Exception {
    ResolverConfiguration configuration = new ResolverConfiguration();
    IProject project = importProject("projects/" + name + "/pom.xml", configuration);
    waitForJobsToComplete();

    project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
    waitForJobsToComplete();

    assertNoErrors(project);
    return project;
  }
}
