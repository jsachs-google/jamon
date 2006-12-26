package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class CallScanner extends AbstractScanner
{
  private final JamonJavaCodeScanner javaScanner = new JamonJavaCodeScanner(JamonColorProvider.instance());
  private final char[] open;
  private final char[] close;
  
  public CallScanner(boolean p_withContent) {
    close = "&>".toCharArray();
    open = (p_withContent ? "<&|" : "<&").toCharArray();
  }
  
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
      tokenLength = 2;
      offset += 2;
      return TAG;
    }

    else if (sawPath)
    {
      if (lookingAt(limit-2, close))
      {
        if (offset == limit - 2)
        {
          tokenLength = 2;
          offset += 2;
          return TAG;
        }
      }

      {
        final IToken tok = javaScanner.nextToken();
        tokenLength = javaScanner.getTokenLength();
        offset  = javaScanner.getTokenOffset();
        offset += tokenLength;
        return tok;
      }
    }
 
    else if (isWhitespace(charAt(offset)))
    {
      offset++;
      while (offset < limit && isWhitespace(charAt(offset)))
      {
        offset++;
      }
      tokenLength = offset - tokenOffset;
      return Token.WHITESPACE;
    }

    else
    {
      offset++;
      while (offset < limit)
      {
        int c = charAt(offset);
        if (isWhitespace(c) || c == ':' || c == ';')
        {
          break;
        }
        offset++;
      }
      tokenLength = offset - tokenOffset;
      sawPath = true;
      javaScanner.setRange(document, offset, limit - offset - 2);
      return PATH;
    }
  }
  
  private boolean isWhitespace(int ch)
  {
    return Character.isWhitespace(ch);
  }
  
  @Override
  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    super.setRange(p_document, p_offset, p_length);
    this.sawPath = false;
  }
  
  static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC));
  static final IToken PATH = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC | SWT.BOLD));
  static final IToken DEFAULT = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(0, 0, 0))));
  
  private boolean sawPath;

}
