package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class CallScanner extends AbstractScanner
{
  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    tokenOffset = offset;
    if (! sawOpen)
    {
      if (lookingAt(offset, "<&".toCharArray()))
      {
        tokenLength = 2;
        offset += 2;
        sawOpen = true;
        return TAG;
      }
      else
      {
        System.err.println("huh!?!");
      }
    }
    if (offset == limit - 2)
    {
      if (lookingAt(offset, "&>".toCharArray()))
      {
        tokenLength = 2;
        offset += 2;
        return TAG;
      }
      else
      {
        System.err.println("huh?!?");
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
    if (! sawPath)
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
      return PATH;
    }
    tokenLength = limit - tokenOffset - 2;
    offset += tokenLength;
    return DEFAULT;
  }
  
  private boolean isWhitespace(int ch)
  {
    return Character.isWhitespace(ch);
  }
  
  @Override
  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    super.setRange(p_document, p_offset, p_length);
    this.sawOpen = false;
    this.sawPath = false;
  }
  
  static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC));
  static final IToken PATH = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC | SWT.BOLD));
  static final IToken DEFAULT = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(0, 0, 0))));
  
  private boolean sawOpen;
  private boolean sawPath;

}
