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
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2003 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson, Matt Raible
 */

package org.jamon.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jamon.compiler.RecompilingTemplateManager;

public class JavaCompilerFactory {
  public static JavaCompiler makeCompiler(
    RecompilingTemplateManager.Data data, String workDir, ClassLoader classLoader) {
    String javac = data.getJavaCompiler();
    List<String> compilerArgs = getCompilerArgs(data, workDir, classLoader);
    if (javac == null) {
      try {
        return new Java6Compiler(compilerArgs);
      }
      catch (Exception e1) {
        // well, we tried
        javac = getDefaultJavac();
      }
    }
    return new ExternalJavaCompiler(javac, compilerArgs);
  }

  private static List<String> getCompilerArgs(
    RecompilingTemplateManager.Data data, String workDir, ClassLoader classLoader) {
    return Collections.unmodifiableList(Arrays.asList(
      "-classpath", getClasspath(workDir, data.getClasspath(), classLoader)));
  }

  /**
   * Get the default location for javaC. Package scoped for unit testing.
   */
  static String getDefaultJavac() {
    // FIXME: does this work on windows?
    // FIXME: should we just use the javac in the default path?
    String bindir;
    if ("Mac OS X".equals(System.getProperty("os.name"))) {
      bindir = "Commands";
    }
    else {
      bindir = "bin";
    }
    String javaHomeParent = new File(System.getProperty("java.home")).getParent();
    return new File(new File(javaHomeParent, bindir), "javac").getAbsolutePath();
  }

  private static void extractClasspath(ClassLoader classLoader, StringBuilder classpath) {
    if (classLoader instanceof URLClassLoader) {
      URL[] urls = ((URLClassLoader) classLoader).getURLs();
      for (int i = 0; i < urls.length; ++i) {
        String url = urls[i].toExternalForm();
        if (url.startsWith("file:")) {
          classpath.append(File.pathSeparator);
          classpath.append(url.substring(5));
        }
      }
    }
    if (classLoader.getParent() != null) {
      extractClasspath(classLoader.getParent(), classpath);
    }
  }

  private static String getClasspath(String start, String classpath, ClassLoader classLoader) {
    StringBuilder cp = new StringBuilder(start);
    if (classpath != null) {
      cp.append(File.pathSeparator);
      cp.append(classpath);
    }

    extractClasspath(classLoader, cp);
    cp.append(File.pathSeparator);
    cp.append(System.getProperty("sun.boot.class.path"));
    cp.append(File.pathSeparator);
    cp.append(System.getProperty("java.class.path"));

    JavaCompilerFactory.pruneJniLibs(cp);

    if (RecompilingTemplateManager.TRACE) {
      RecompilingTemplateManager.trace("Jamon compilation CLASSPATH is " + cp);
    }

    return cp.toString();
  }

  private static void pruneJniLibs(StringBuilder cp) {
    String[] components = cp.toString().split(File.pathSeparator);
    cp.delete(0, cp.length());
    boolean first = true;
    for (String c : components) {
      if (!c.endsWith(".jnilib") && !c.endsWith(".dylib")) {
        if (!first) {
          cp.append(File.pathSeparator);
        }
        first = false;
        cp.append(c);
      }
    }
  }
}
