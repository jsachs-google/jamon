package org.jamon.eclipse;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class TemplateResourcesTest extends TestCase {
private static final String GEN_SOURCES_DIR = "genSources";
  private static final String PROJECT_NAME = "foo";
  private static final String PROJECT_PATH = "/" + PROJECT_NAME + "/";
  private static final String GEN_SOURCES_PATH = PROJECT_PATH + GEN_SOURCES_DIR;
  private static final Path TEMPLATE_PATH = new Path("org/jamon/ATemplate");
  private IWorkspaceRoot m_root;

  @Override protected void setUp() throws Exception {
    m_root = ResourcesPlugin.getWorkspace().getRoot();
  }

  public void testOriginalConstructor() {
    checkTemplateResources(new TemplateResources(
      makeFile("/foo/templates/org/jamon/ATemplate.jamon"), sourceFolder(), templateFolder()), "templates", GEN_SOURCES_DIR);
  }

  public void testNewConstructor() {
    checkTemplateResources(new TemplateResources(TEMPLATE_PATH, sourceFolder(), templateFolder()), "templates", GEN_SOURCES_DIR);
  }

  public void testFromGeneratedProxy() {
    checkTemplateResources(TemplateResources.fromGeneratedSource(
      makeFile(GEN_SOURCES_PATH + "/org/jamon/ATemplate.java"), sourceFolder(), templateFolder()),
      "templates", GEN_SOURCES_DIR);
  }

  public void testFromGeneratedImpl() {
    checkTemplateResources(TemplateResources.fromGeneratedSource(
      makeFile(GEN_SOURCES_PATH + "/org/jamon/ATemplateImpl.java"), sourceFolder(), templateFolder()),
      "templates", GEN_SOURCES_DIR);
  }

  public void testNotFromGeneratedSource() {
    assertNull(TemplateResources.fromGeneratedSource(
      makeFile(GEN_SOURCES_PATH + "/org/jamon/File.notjava"),
      sourceFolder(),
      makeFolder("/bar/templates")));

    assertNull(TemplateResources.fromGeneratedSource(
      makeFile("/foo/notGenSources/org/jamon/File.java"),
      sourceFolder(),
      makeFolder("/bar/templates")));
  }

  public void testFromGeneratedSource() throws Exception { //FIXME - change test name
      IProject project = makeProject();
      checkTemplateResources(
        TemplateResources.fromGeneratedSource(makeFile(GEN_SOURCES_PATH + "/org/jamon/ATemplate.java")),
        "templates", GEN_SOURCES_DIR);
      checkTemplateResources(
        TemplateResources.fromGeneratedSource(makeFile(GEN_SOURCES_PATH + "/org/jamon/ATemplateImpl.java")),
        "templates", GEN_SOURCES_DIR);
      project.close(null);
      project.delete(true, true, null);
  }

  private IProject makeProject() throws CoreException, JavaModelException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProject project = workspace.getRoot().getProject(PROJECT_NAME);
    project.delete(true, true, null); // just in case
    IProjectDescription description = workspace.newProjectDescription(project.getName());

    description.setLocation(null);
    List<String> natures = new ArrayList<String>();
    for (String nature : description.getNatureIds()) {
      natures.add(nature);
    }
    natures.add(JavaCore.NATURE_ID);
    description.setNatureIds(natures.toArray(new String[natures.size()]));
    project.create(description, null);
    project.open(null);
    ((IJavaProject) project.getNature(JavaCore.NATURE_ID)).setRawClasspath(
      new IClasspathEntry[] { JavaCore.newSourceEntry(new Path("/foo/src")) }, null);
    JamonNature.addToProject(
        project,
        "templates",
        GEN_SOURCES_DIR,
        new ProcessorJarLocations().withProcessorSourceType(ProcessorSourceType.PLUGIN));
    return project;
  }

  private void checkTemplateResources(
    TemplateResources resources, String templateDir, String genSourcesDir) {
    assertNotNull(resources);
    assertEquals(TEMPLATE_PATH, resources.getPath());
    assertEquals(
      makeFile(PROJECT_PATH + templateDir + "/org/jamon/ATemplate.jamon"), resources.getTemplate());
    assertEquals(
      makeFile(PROJECT_PATH + genSourcesDir + "/org/jamon/ATemplate.java"), resources.getProxy());
    assertEquals(
      makeFile(PROJECT_PATH + genSourcesDir + "/org/jamon/ATemplateImpl.java"), resources.getImpl());
  }

  private IFile makeFile(String p_path) {
    return m_root.getFile(new Path(p_path));
  }

  private IFolder makeFolder(String p_path) {
    return m_root.getFolder(new Path(p_path));
  }

  private IFolder templateFolder() {
    return makeFolder("/foo/templates");
  }

  private IFolder sourceFolder() {
    return makeFolder("/foo/genSources");
  }
}
