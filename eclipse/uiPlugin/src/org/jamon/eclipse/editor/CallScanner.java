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
  
  private static final char[] OPEN = "<&".toCharArray();
  private static final char[] CLOSE = "&>".toCharArray();
  
  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    tokenOffset = offset;

    if (initialOffset == offset)
    {
      Assert.isTrue(lookingAt(offset, OPEN));
      tokenLength = 2;
      offset += 2;
      return TAG;
    }

    if (sawPath)
    {
      if (lookingAt(limit-2, CLOSE))
      {
        if (offset == limit - 2)
        {
          tokenLength = 2;
          offset += 2;
          return TAG;
        }
        else
        {
          tokenLength = limit - tokenOffset - 2;
          offset += tokenLength;
          return DEFAULT;
        }
      }
      else
      {
        // partial
        tokenLength = limit - tokenOffset;
        offset += tokenLength;
        return DEFAULT;
      }
    }
 
    if (isWhitespace(charAt(offset)))
    {
      offset++;
      while (offset < limit && isWhitespace(charAt(offset)))
      {
        offset++;
      }
      tokenLength = offset - tokenOffset;
      return Token.WHITESPACE;
    }

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
    return PATH;
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
