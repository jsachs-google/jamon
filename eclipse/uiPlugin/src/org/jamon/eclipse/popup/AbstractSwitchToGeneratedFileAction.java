package org.jamon.eclipse.popup;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorPart;
import org.jamon.eclipse.EclipseUtils;
import org.jamon.eclipse.JamonEditor;
import org.jamon.eclipse.JamonUtils;
import org.jamon.eclipse.TemplateResources;

public abstract class AbstractSwitchToGeneratedFileAction
    extends AbstractSwitchToFileAction
{
    private IEditorPart m_editor;

    public void setActiveEditor(IAction p_action, IEditorPart p_targetEditor)
    {
        m_editor = p_targetEditor;
    }

    public void run(IAction p_action)
    {
        if (m_editor instanceof JamonEditor)
        {
            try
            {
                JamonEditor jamonEditor = (JamonEditor) m_editor;
                TemplateResources  templateResources = jamonEditor.getTemplateResources();
                IFile generatedFile = getGeneratedFile(templateResources);

                switchToFile(
                    generatedFile,
                    JamonUtils.getBestMatchJavaLine(
                        generatedFile, jamonEditor.getCursorLocation()));
            }
            catch (BadLocationException e)
            {
                EclipseUtils.logError(e);
            }
            catch (CoreException e)
            {
                EclipseUtils.logError(e);
            }
            catch (IOException e)
            {
                EclipseUtils.logError(e);
            }
        }

    }

    protected abstract IFile getGeneratedFile(
        TemplateResources p_templateResources);
}
