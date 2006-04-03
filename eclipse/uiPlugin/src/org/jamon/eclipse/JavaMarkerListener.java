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
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */
package org.jamon.eclipse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class JavaMarkerListener implements IResourceChangeListener
{
    public JavaMarkerListener(final IFolder p_templateFolder,
                              final IFolder p_generatedSourcesFolder)
    {
        m_templateFolder = p_templateFolder;
        m_generatedSourcesFolder = p_generatedSourcesFolder;
    }

    public class ResourceDeltaVisitor implements IResourceDeltaVisitor
    {
        public boolean visit(IResourceDelta p_delta) throws CoreException
        {
            if (!m_generatedSourcesFolder.getFullPath()
                    .isPrefixOf(p_delta.getFullPath())
                && !p_delta.getFullPath()
                    .isPrefixOf(m_generatedSourcesFolder.getFullPath()))
            {
                return false;
            }
            if (p_delta.getKind() == IResourceDelta.REMOVED)
            {
                return false;
            }
            if (p_delta.getResource().getType() == IResource.FILE
                && "java".equals(p_delta.getFullPath().getFileExtension()))
            {
                GeneratedResource generatedResource =
                    new GeneratedResource((IFile) p_delta.getResource());
                IMarkerDelta[] markerDeltas = p_delta.getMarkerDeltas();

                String markerType = 
                  generatedResource.isImpl() ? implMarkerId : proxyMarkerId;
                generatedResource.getTemplateFile().deleteMarkers(
                    markerType, true, IResource.DEPTH_ZERO);

                for (IMarkerDelta markerDelta : markerDeltas)
                {
                    switch(markerDelta.getKind())
                    {
                    case IResourceDelta.ADDED:
                    case IResourceDelta.CHANGED:
                    {
                        IMarker marker = markerDelta.getMarker();
                        copyMarker(generatedResource, marker, markerType);
                    }
                    break;
                    }
                }
            }
            return true;
        }

        private void copyMarker(GeneratedResource p_generatedResource,
                                  IMarker p_marker, 
                                  String p_markerType) throws CoreException
        {
            if (p_marker.isSubtypeOf(IMarker.PROBLEM))
            {
                String message = p_marker.getAttribute(IMarker.MESSAGE, null);

                if (message != null && message.length() > 0
                    && !isMarkerProxyImportWarning(p_generatedResource, message)
                    && !isMarkerIOExceptionNotThrownWarning(message))
                {
                    EclipseUtils.populateProblemMarker(
                        p_generatedResource
                            .getTemplateFile()
                            .createMarker(p_markerType),
                        p_generatedResource.getTemplateLineNumber(
                            p_marker.getAttribute(IMarker.LINE_NUMBER, 1)),
                        (p_generatedResource.isImpl() ? "Impl: " : "Proxy: ")
                        + message,
                        p_marker.getAttribute(
                            IMarker.SEVERITY, IMarker.SEVERITY_ERROR));
                }
                p_marker.delete();
            }
        }

        private boolean isMarkerIOExceptionNotThrownWarning(String message) {
          // It's not uncommon to have an empty method definition in a 
          // parent; since there are no statements, it cannot throw an 
          // IOException, but children might.  Eclipse is not smart enough
          // to not warn about this.
          return message.startsWith(
            "The declared exception IOException is not actually thrown by the method __jamon_innerUnit__");
        }

        
        private boolean isMarkerProxyImportWarning(GeneratedResource p_generatedResource, String message) {
          // We place all imports in both the proxy and impl; in general, 
          // the proxy will not need many of these.
          return !p_generatedResource.isImpl()
              && message.startsWith("The import ")
              && message.endsWith(" is never used");
        }

        private class GeneratedResource
        {
            public GeneratedResource(IFile p_generatedJavaFile)
                throws CoreException
            {
                IPath path = p_generatedJavaFile.getFullPath();
                String className = path.removeFileExtension().lastSegment();
                if (className.endsWith("Impl"))
                {
                    className = className.substring(0, className.length() - 4);
                    m_isImpl = true;
                }
                else
                {
                    m_isImpl = false;
                }
                m_templateFile = m_templateFolder.getFile(
                    path
                        .removeFirstSegments(
                            m_generatedSourcesFolder.getFullPath().segmentCount())
                        .removeLastSegments(1)
                        .append(className)
                        .addFileExtension(JamonNature.JAMON_EXTENSION));

                try
                {
                    m_locations = JamonUtils.readLineNumberMappings(p_generatedJavaFile);
                }
                catch (IOException e)
                {
                    m_locations = Collections.emptyList();
                    JamonProjectPlugin.getDefault().logError(e);
                }
            }


            public int getTemplateLineNumber(int p_javaLineNumber)
            {
                if (m_locations.isEmpty())
                {
                    return 1;
                }
                else
                {
                    return m_locations.get(
                        Math.min(p_javaLineNumber, m_locations.size() - 1));
                }
            }

            public boolean isImpl()
            {
                return m_isImpl;
            }

            public IFile getTemplateFile()
            {
                return m_templateFile;
            }

            private final IFile m_templateFile;
            private final boolean m_isImpl;
            private List<Integer> m_locations;
        }
    }

    public void resourceChanged(IResourceChangeEvent p_event)
    {
        try
        {
            p_event.getDelta().accept(new ResourceDeltaVisitor());
        }
        catch (CoreException e)
        {
            JamonProjectPlugin.getDefault().logError(e);
        }
    }

    private final IFolder m_templateFolder, m_generatedSourcesFolder;
    private final String proxyMarkerId = JamonProjectPlugin.getProxyMarkerType();
    private final String implMarkerId = JamonProjectPlugin.getImplMarkerType();
}
