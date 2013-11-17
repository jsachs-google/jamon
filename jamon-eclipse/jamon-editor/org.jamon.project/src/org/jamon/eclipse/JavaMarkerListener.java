/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;


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

public class JavaMarkerListener implements IResourceChangeListener
{
    public JavaMarkerListener(final IFolder p_generatedSourcesFolder)
    {
        m_generatedSourcesFolder = p_generatedSourcesFolder;
    }

    public class ResourceDeltaVisitor implements IResourceDeltaVisitor
    {
        @Override
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
                GeneratedResource generatedResource = new GeneratedResource(
                  (IFile) p_delta.getResource());
                IMarkerDelta[] markerDeltas = p_delta.getMarkerDeltas();

                String markerType =
                  generatedResource.isImpl() ? implMarkerId : proxyMarkerId;
                if (markerDeltas.length > 0)
                {
                    generatedResource.getTemplateFile().deleteMarkers(
                        markerType, true, IResource.DEPTH_ZERO);
                }
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
                    && !isMarkerProxyImportWarning(p_generatedResource, message))
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

        private boolean isMarkerProxyImportWarning(GeneratedResource p_generatedResource, String message) {
          // We place all imports in both the proxy and impl; in general,
          // the proxy will not need many of these.
          return !p_generatedResource.isImpl()
              && message.startsWith("The import ")
              && message.endsWith(" is never used");
        }
    }

    @Override
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

    private final IFolder m_generatedSourcesFolder;
    private final String proxyMarkerId = JamonProjectPlugin.getProxyMarkerType();
    private final String implMarkerId = JamonProjectPlugin.getImplMarkerType();
}
