/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

public class JamonAnnotationHover implements IAnnotationHover
{
    protected List<Annotation> getAnnotationsForLine(
        ISourceViewer p_viewer, int p_line)
    {
        //FIXME - should we only be looking at jamon-generated annotations?
        IDocument document= p_viewer.getDocument();
        IAnnotationModel model= p_viewer.getAnnotationModel();

        if (model == null)
            return null;

        List<Annotation> exact = new ArrayList<Annotation>();

        for (@SuppressWarnings("unchecked") Iterator<Annotation> i
                = model.getAnnotationIterator(); i.hasNext(); )
        {
            Annotation annotation = i.next();
            try
            {
                if (annotation.getText() != null
                    && document.getLineOfOffset(
                        model.getPosition(annotation).getOffset()) ==  p_line)
                {
                    exact.add(annotation);
                }
            }
            catch (BadLocationException e)
            {
                //FIXME - really ok to ignore this?
            }
        }
        return exact;
    }

    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber)
    {
        Iterable<Annotation> annotations =
            getAnnotationsForLine(sourceViewer, lineNumber);
        if (annotations != null)
        {
            StringBuffer messages = new StringBuffer();
            boolean printedLines = false;
            for (Annotation annotation : annotations)
            {
                String message= annotation.getText();
                if (message != null && message.trim().length() > 0)
                {
                    if (printedLines)
                    {
                        messages.append("\n");
                    }
                    messages.append(message.trim());
                    printedLines = true;
                }
            }
            return messages.toString();
        }
        return null;
    }
}
