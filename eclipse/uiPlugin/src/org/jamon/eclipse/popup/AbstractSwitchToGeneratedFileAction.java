package org.jamon.eclipse.popup;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.jamon.eclipse.EclipseUtils;
import org.jamon.eclipse.JamonEditor;
import org.jamon.eclipse.JamonUtils;
import org.jamon.eclipse.Location;
import org.jamon.eclipse.TemplateResources;

public abstract class AbstractSwitchToGeneratedFileAction
    extends AbstractSwitchToFileAction
{
    public static TemplateResources getTemplateResources(IEditorPart editor)
        throws CoreException
    {
        if (editor.getEditorInput() instanceof IFileEditorInput)
        {
            return TemplateResources.fromTemplate(
                ((IFileEditorInput)editor.getEditorInput()).getFile());
        }
        else
        {
            return null;
        }
    }

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
                TemplateResources templateResources = getTemplateResources(jamonEditor);
                IFile generatedFile = getGeneratedFile(templateResources);

                ISelection selection = jamonEditor.getSelectionProvider().getSelection();
                if (selection instanceof ITextSelection)
                {
                    ITextSelection textSelection = (ITextSelection) selection;
                    int line = textSelection.getStartLine();
                    int offset = textSelection.getOffset();
                    int column = offset - getDocument(jamonEditor).getLineOffset(line);
                    switchToFile(
                        generatedFile,
                        JamonUtils.getBestMatchJavaLine(
                            generatedFile, new Location(line + 1, column + 1)));
                }
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
