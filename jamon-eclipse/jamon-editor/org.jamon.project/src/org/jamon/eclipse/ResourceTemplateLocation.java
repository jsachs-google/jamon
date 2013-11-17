/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import org.eclipse.core.resources.IFile;
import org.jamon.api.TemplateLocation;

public class ResourceTemplateLocation implements TemplateLocation
{
    public ResourceTemplateLocation(IFile p_file)
    {
        m_file = p_file;
    }
    
    @Override public String toString()
    {
        return m_file.getFullPath().toString();
    }
    
    public IFile getFile()
    {
        return m_file;
    }

    private final IFile m_file;
}
