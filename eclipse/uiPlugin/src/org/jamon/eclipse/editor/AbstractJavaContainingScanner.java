package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public abstract class AbstractJavaContainingScanner extends AbstractScanner
{
  private final JamonJavaCodeScanner javaScanner;
  
  protected AbstractJavaContainingScanner(String p_openTag, String p_closeTag, RGB fgColor, RGB bgColor) {
    open = p_openTag.toCharArray();
    close = p_closeTag.toCharArray();
    tagToken = new Token(new TextAttribute(JamonColorProvider.instance().getColor(fgColor), JamonColorProvider.instance().getColor(bgColor), SWT.NORMAL));
    javaScanner  = new JamonJavaCodeScanner(JamonColorProvider.instance(), bgColor);
  }
  
  private final char[] open;
  private final char[] close;
  private final IToken tagToken;
  
  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    tokenOffset = offset;

    if (initialOffset == offset)
    {
      Assert.isTrue(lookingAt(offset, open));
      tokenLength = open.length;
      offset += open.length;
      javaScanner.setRange(document, offset, limit - offset - open.length);
      return tagToken;
    }

    else if (lookingAt(limit-close.length, close) && (offset == limit - close.length))
    {
      tokenLength = close.length;
      offset += close.length;
      return tagToken;
    }
    else
    {
      final IToken tok = javaScanner.nextToken();
      tokenLength = javaScanner.getTokenLength();
      offset  = javaScanner.getTokenOffset();
      offset += tokenLength;
      return tok;
    }
  }
  
}
