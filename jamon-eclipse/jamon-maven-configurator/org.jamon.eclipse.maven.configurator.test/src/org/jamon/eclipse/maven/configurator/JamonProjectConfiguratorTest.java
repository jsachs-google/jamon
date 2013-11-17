/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
  public void testBasicProject() throws Exception {
    IProject project = importAndBuildProject("basic-jamon-project");
    assertTrue(JamonNature.projectHasNature(project));
    assertEquals("src/main/templates", JamonNature.templateSourceFolderName(project));
    assertEquals("target/generated-sources/jamon", JamonNature.templateOutputFolderName(project));
  }

  @Test
  public void testCustomizedProject() throws Exception {
    IProject project = importAndBuildProject("customized-jamon-project");
    assertTrue(JamonNature.projectHasNature(project));
    assertEquals("src/special", JamonNature.templateSourceFolderName(project));
    assertEquals("tsrc", JamonNature.templateOutputFolderName(project));
  }

  @Test
  public void testTestProject() throws Exception {
    IProject project = importAndBuildProject("jamon-project-in-tests");
    assertTrue(JamonNature.projectHasNature(project));
    assertEquals("src/test/templates", JamonNature.templateSourceFolderName(project));
    assertEquals("target/generated-test-sources/jamon", JamonNature.templateOutputFolderName(project));
  }

  @Test
  public void testCustomizedTestProject() throws Exception {
    IProject project = importAndBuildProject("customized-jamon-project-in-tests");
    assertTrue(JamonNature.projectHasNature(project));
    assertEquals("src/test/special", JamonNature.templateSourceFolderName(project));
    assertEquals("tsrc", JamonNature.templateOutputFolderName(project));
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
