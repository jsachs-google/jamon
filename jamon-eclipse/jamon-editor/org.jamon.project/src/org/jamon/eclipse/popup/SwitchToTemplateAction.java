/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.popup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.jamon.eclipse.EclipseUtils;
import org.jamon.eclipse.GeneratedResource;
import org.jamon.eclipse.NotAGeneratedResourceException;

public class SwitchToTemplateAction extends AbstractSwitchToFileAction
   {
  private IEditorPart m_editor;

  @Override
  public void setActiveEditor(IAction p_action, IEditorPart p_targetEditor)
  {
      m_editor = p_targetEditor;
  }

  @Override
  public void run(IAction p_action) {
    try {
      if (m_editor instanceof AbstractTextEditor) {
        AbstractTextEditor textEditor = (AbstractTextEditor) m_editor;
        if (textEditor.getEditorInput() instanceof IFileEditorInput) {
            GeneratedResource generatedResource =
                new GeneratedResource(((IFileEditorInput) textEditor.getEditorInput()).getFile());
            ISelection selection = textEditor.getSelectionProvider().getSelection();
            if (selection instanceof ITextSelection)
            {
                switchToFile(
                    generatedResource.getTemplateFile(),
                    generatedResource.getTemplateLineNumber(
                        ((ITextSelection) selection).getStartLine()));
            }
        }
      }
    }
    catch (NotAGeneratedResourceException e) {
        EclipseUtils.logError(e);
    }
    catch (CoreException e) {
      EclipseUtils.logError(e);
    }
    catch (BadLocationException e) {
      EclipseUtils.logError(e);
    }
  }
}
