/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jamon.api.TemplateParser;
import org.jamon.api.TemplateSource;

/**
 * A classloader using classes from the runtime classpath of a java project.
 */
public class ProjectClassLoader extends URLClassLoader
{
    private final static String PARSER_IMPL_NAME = "org.jamon.codegen.TemplateParserImpl";

    public ProjectClassLoader(IJavaProject p_project, File p_processorJar)
    throws CoreException
    {
        super(classpathUrlArray(p_project, p_processorJar), TemplateParser.class.getClassLoader());
        try
        {
            @SuppressWarnings("unchecked") Class<? extends TemplateParser> templateParserImplClass =
                (Class<? extends TemplateParser>) loadClass(PARSER_IMPL_NAME);
            if (TemplateParser.class.isAssignableFrom(templateParserImplClass))
            {
                m_templateParserConstructor =
                    templateParserImplClass.getConstructor(TemplateSource.class, ClassLoader.class);
            }
            else
            {
                throw EclipseUtils.createCoreException(
                    p_processorJar.getAbsolutePath() + " does not contain a template parser");
            }
        }
        catch (Throwable e)
        {
            throw EclipseUtils.createCoreException(e);
        }
    }

    /**
     * Determine if the given file is a jamon processor jar
     * @param p_processorJar the file to test
     * @return {@code true} if the file appears to be a jamon-processor jar, or {@code false}
     * otherwise
     */
    public static boolean verifyProcessorJar(File p_processorJar)
    {
        try
        {
            return TemplateParser.class.isAssignableFrom(
                new URLClassLoader(
                    new URL[] { p_processorJar.toURI().toURL() },
                    TemplateParser.class.getClassLoader())
                    .loadClass(PARSER_IMPL_NAME));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Determine if the project has the runtime jar in its classpath
     * @return {@code true} if it has a runtime jar
     * @throws CoreException
     */
    public boolean hasRuntimeJar()
    {
        try
        {
            loadClass("org.jamon.Renderer");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Create a {@code TemplateParser} instance
     * @param p_templateSource the {@code TemplateSource} for the parser
     * @return a new parser instance
     * @throws CoreException if there are issues loading the implementation class, or creating an
     * instance
     */
    public TemplateParser createTemplateParser(TemplateSource p_templateSource) throws CoreException
    {
        try
        {
            return m_templateParserConstructor.newInstance(p_templateSource, this);
        }
        catch (Exception e)
        {
            throw EclipseUtils.createCoreException(e);
        }
    }

    private static URL[] classpathUrlArray(IJavaProject p_project, File p_processorJar)
    throws CoreException
    {
        Set<IJavaProject> visitedProjects = new HashSet<IJavaProject>();
        try
        {
            List<URL> urls = classpathUrlsForProject(p_project, visitedProjects);
            urls.add(p_processorJar.toURI().toURL());
            return urls.toArray(new URL[urls.size()]);
        }
        catch (MalformedURLException e)
        {
            throw EclipseUtils.createCoreException(e);
        }
    }

    private static List<URL> classpathUrlsForProject(
      IJavaProject p_project, Set<IJavaProject> visitedProjects)
    throws CoreException, MalformedURLException
    {
        if (visitedProjects.add(p_project)) {
          List<URL> urls = new ArrayList<URL>();
          for (String entry : JavaRuntime.computeDefaultRuntimeClassPath(p_project))
          {
            urls.add(new File(entry).toURI().toURL());
          }
          addDependenciesClasspath(p_project, urls, visitedProjects);
          return urls;
        }
        else {
          // Avoid going in circles. See bug 2807733
          return Collections.emptyList();
        }
    }

    private static void addDependencyClasspath(
        IProject p_project, List<URL> urls, Set<IJavaProject> visitedProjects)
    throws CoreException, MalformedURLException
    {
        if (! p_project.isOpen())
        {
            EclipseUtils.logInfo(
                "Apparently dependent project " + p_project.getName() + " not open, ignoring");
            return;
        }
        if (! p_project.hasNature(JavaCore.NATURE_ID))
        {
            EclipseUtils.logInfo(
                "Dependent project " + p_project.getName() + " not a java project, ignoring");
            return;
        }
        urls.addAll(classpathUrlsForProject(
          (IJavaProject) p_project.getNature(JavaCore.NATURE_ID), visitedProjects));
    }

    private static void addDependenciesClasspath(
        IJavaProject p_project, List<URL> urls, Set<IJavaProject> visitedProjects)
    throws CoreException, MalformedURLException
    {
        for (final IProject proj : p_project.getProject().getReferencedProjects())
        {
            addDependencyClasspath(proj, urls, visitedProjects);
        }
    }

    private Constructor<? extends TemplateParser> m_templateParserConstructor;
}
