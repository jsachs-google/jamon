package org.jamon.eclipse.popup;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractSwitchToFileAction implements IEditorActionDelegate
{

    protected void switchToFile(IFile targetFile, int targetLineNumber)
        throws PartInitException, BadLocationException
    {
          IEditorPart editorPart = IDE.openEditor(
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
              targetFile);

          if (editorPart instanceof ITextEditor)
          {
              ITextEditor editor = (ITextEditor)editorPart;
              IDocument document = getDocument(editor);
              int offset = document.getLineOffset(targetLineNumber);
              editor.selectAndReveal(offset, 0);
          }
      }

    protected IDocument getDocument(ITextEditor editor)
    {
        return editor.getDocumentProvider()
              .getDocument(editor.getEditorInput());
    }

    @Override
    public void selectionChanged(IAction p_action, ISelection p_selection)
    {}

}
