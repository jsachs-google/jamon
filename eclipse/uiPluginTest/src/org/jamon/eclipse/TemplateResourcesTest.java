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
  private static final Path TEMPLATE_PATH = new Path("org/jamon/ATemplate");
  private IWorkspaceRoot m_root;

  @Override protected void setUp() throws Exception {
    m_root = ResourcesPlugin.getWorkspace().getRoot();
  }

  public void testOriginalConstructor() {
    checkTemplateResources(new TemplateResources(
      makeFile("/foo/templates/org/jamon/ATemplate.jamon"), sourceFolder(), templateFolder()), "templates", "genSources");
  }

  public void testNewConstructor() {
    checkTemplateResources(new TemplateResources(TEMPLATE_PATH, sourceFolder(), templateFolder()), "templates", "genSources");
  }

  public void testFromGeneratedProxy() {
    checkTemplateResources(TemplateResources.fromGeneratedSource(
      makeFile("/foo/genSources/org/jamon/ATemplate.java"), sourceFolder(), templateFolder()), "templates", "genSources");
  }

  public void testFromGeneratedImpl() {
    checkTemplateResources(TemplateResources.fromGeneratedSource(
      makeFile("/foo/genSources/org/jamon/ATemplateImpl.java"), sourceFolder(), templateFolder()), "templates", "genSources");
  }

  public void testNotFromGeneratedSource() {
    assertNull(TemplateResources.fromGeneratedSource(
      makeFile("/foo/genSources/org/jamon/File.notjava"),
      sourceFolder(),
      makeFolder("/bar/templates")));

    assertNull(TemplateResources.fromGeneratedSource(
      makeFile("/foo/genSourcesss/org/jamon/File.java"),
      sourceFolder(),
      makeFolder("/bar/templates")));
  }

  public void testFromGeneratedSource() throws Exception { //FIXME - change test name
      IProject project = makeProject();
      checkTemplateResources(
        TemplateResources.fromGeneratedSource(makeFile("/foo/tsrc/org/jamon/ATemplate.java")),
        "templates", "tsrc");
      checkTemplateResources(
        TemplateResources.fromGeneratedSource(makeFile("/foo/tsrc/org/jamon/ATemplateImpl.java")),
        "templates", "tsrc");
      project.close(null);
      project.delete(true, true, null);
  }

  private IProject makeProject() throws CoreException, JavaModelException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProject project = workspace.getRoot().getProject("foo");
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
    JamonNature.addToProject(project, "templates");
    return project;
  }

  private void checkTemplateResources(
    TemplateResources resources, String templateDir, String genSourcesDir) {
    assertNotNull(resources);
    assertEquals(TEMPLATE_PATH, resources.getPath());
    assertEquals(
      makeFile("/foo/" + templateDir + "/org/jamon/ATemplate.jamon"), resources.getTemplate());
    assertEquals(
      makeFile("/foo/" + genSourcesDir + "/org/jamon/ATemplate.java"), resources.getProxy());
    assertEquals(
      makeFile("/foo/" + genSourcesDir + "/org/jamon/ATemplateImpl.java"), resources.getImpl());
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
