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
import org.eclipse.ui.texteditor.AbstractTextEditor;

public abstract class AbstractSwitchToFileAction implements IEditorActionDelegate
{

    protected void switchToFile(IFile targetFile, int targetLineNumber)
        throws PartInitException, BadLocationException
    {
          IEditorPart editorPart = IDE.openEditor(
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
              targetFile);

          if (editorPart instanceof AbstractTextEditor)
          {
              AbstractTextEditor jamonEditor = (AbstractTextEditor)editorPart;
              IDocument jamonDocument = jamonEditor.getDocumentProvider()
                  .getDocument(jamonEditor.getEditorInput());
              int offset = jamonDocument.getLineOffset(targetLineNumber);
              jamonEditor.selectAndReveal(offset, 0);
          }
      }

    public void selectionChanged(IAction p_action, ISelection p_selection)
    {}

}
