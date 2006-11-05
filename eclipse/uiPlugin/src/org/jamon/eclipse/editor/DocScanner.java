package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class DocScanner extends AbstractScanner
{
  private static final char[] CLOSE = "</%doc>".toCharArray();
  private static final char[] OPEN = "<%doc>".toCharArray();

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }

    tokenOffset = offset;
    
    if (offset == initialOffset)
    {
      Assert.isTrue(lookingAt(offset, OPEN));
      tokenLength = OPEN.length;
      offset += tokenLength;
      return TAG;
    }

    if (lookingAt(limit - CLOSE.length, CLOSE))
    {
      if (offset == limit - CLOSE.length)
      {
        offset = limit; 
        tokenLength = CLOSE.length;
        return TAG;
      }
      else
      {
        offset = limit - CLOSE.length;
        tokenLength = limit - CLOSE.length - tokenOffset;
        return DOC;
      }
    }
    else
    {
      offset = limit;
      tokenLength = limit - tokenOffset;
      return DOC;
    }

  }


  static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 127, 127)), null, SWT.BOLD));
  static final IToken DOC = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(112, 112, 112)), null, SWT.ITALIC));
}
