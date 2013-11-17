/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EclipseUtils
{
    public static void delete(IFile p_file) throws CoreException
    {
        if (p_file.exists())
        {
            EclipseUtils.unsetReadOnly(p_file);
            p_file.delete(true, null);
        }
    }

    public static void closeQuiety(InputStream is)
    {
        if (is == null)
        {
            return;
        }
        try
        {
            is.close();
        }
        catch (IOException e)
        {
            // sshh
        }
    }

    public static void logError(Throwable p_error) {
        JamonProjectPlugin.getDefault().logError(p_error);
    }

    public static void logInfo(String p_message) {
        JamonProjectPlugin.getDefault().logInfo(p_message);
    }

    public static CoreException createCoreException(Throwable e)
    {
        return new CoreException(new Status(
            IStatus.ERROR,
            JamonProjectPlugin.getDefault().getBundle().getSymbolicName(),
            0,
            e.getMessage() != null ? e.getMessage() : e.getClass().getName(),
            e));
    }

    public static CoreException createCoreException(String message)
    {
        return new CoreException(new Status(
            IStatus.ERROR,
            JamonProjectPlugin.getDefault().getBundle().getSymbolicName(),
            0,
            message,
            null));
    }

    private EclipseUtils() {}

    public static void populateProblemMarker(
        IMarker p_marker, int p_lineNumber, String p_message, int p_severity)
        throws CoreException
    {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IMarker.LINE_NUMBER, new Integer(p_lineNumber));
        attributes.put(IMarker.MESSAGE, p_message);
        attributes.put(IMarker.SEVERITY, new Integer(p_severity));
        p_marker.setAttributes(attributes);
    }

    public static void unsetReadOnly(IResource p_resource) throws CoreException
    {
        ResourceAttributes resourceAttributes =
            p_resource.getResourceAttributes();
        if (resourceAttributes != null && resourceAttributes.isReadOnly())
        {
            resourceAttributes.setReadOnly(false);
            p_resource.setResourceAttributes(resourceAttributes);
        }
    }
}
