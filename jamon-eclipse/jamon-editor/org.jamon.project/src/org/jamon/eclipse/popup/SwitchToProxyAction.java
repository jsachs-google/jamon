/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.popup;

import org.eclipse.core.resources.IFile;
import org.jamon.eclipse.TemplateResources;

public class SwitchToProxyAction extends AbstractSwitchToGeneratedFileAction
{
    @Override
    protected IFile getGeneratedFile(TemplateResources p_templateResources)
    {
        return p_templateResources.getProxy();
    }

}
