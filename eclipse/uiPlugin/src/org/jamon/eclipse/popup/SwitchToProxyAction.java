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
