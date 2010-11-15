package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DocumentMocker
{
  public static IDocument createMockDocument(final String content)
  {
    IDocument doc = Mockito.mock(IDocument.class);
    Mockito.when(doc.getLength()).thenReturn(content.length());
    try
    {
      Mockito.when(doc.getChar(Mockito.anyInt())).thenAnswer(new Answer<Character>() {
        public Character answer(InvocationOnMock p_invocation) throws Throwable
        {
          return content.charAt((Integer) p_invocation.getArguments()[0]);
        }});
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    Mockito.when(doc.getLegalLineDelimiters()).thenReturn(new String[0]);
    return doc;
  }
}
