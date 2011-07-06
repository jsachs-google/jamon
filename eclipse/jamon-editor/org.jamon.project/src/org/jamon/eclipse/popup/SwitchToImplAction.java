package org.jamon.eclipse.popup;

import org.eclipse.core.resources.IFile;
import org.jamon.eclipse.TemplateResources;

public class SwitchToImplAction extends AbstractSwitchToGeneratedFileAction
{
    @Override
    protected IFile getGeneratedFile(TemplateResources p_templateResources)
    {
        return p_templateResources.getImpl();
    }
}
