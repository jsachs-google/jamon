package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class CallScanner implements ITokenScanner
{
  public int getTokenLength()
  {
    return tokenLength;
  }
  
  public int getTokenOffset()
  {
    return tokenOffset;
  }

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    if (! sawOpen)
    {
      if (lookingAt(offset, "<&".toCharArray()))
      {
        tokenOffset = offset;
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
        tokenOffset = offset;
        tokenLength = 2;
        offset += 2;
        return TAG;
      }
      else
      {
        System.err.println("huh?!?");
      }
    }
    if (isWhitespace(nextChar(offset)))
    {
      tokenOffset = offset++;
      while (offset < limit && isWhitespace(nextChar(offset)))
      {
        offset++;
      }
      tokenLength = offset - tokenOffset;
      return Token.WHITESPACE;
    }
    if (! sawPath)
    {
      tokenOffset = offset++;
      while (offset < limit)
      {
        int c = nextChar(offset);
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
    tokenOffset = offset;
    tokenLength = limit - tokenOffset - 2;
    offset += tokenLength;
    return DEFAULT;
  }
  
  private boolean isWhitespace(int ch)
  {
    return Character.isWhitespace(ch);
  }
  
  private static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC));
  private static final IToken PATH = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 31, 15)), null, SWT.ITALIC | SWT.BOLD));
  private static final IToken DEFAULT = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(0, 0, 0))));
  
  private boolean sawOpen;
  private boolean sawPath;
  
  private int nextChar(int i)
  {
    try
    {
      return document.getChar(i);
    }
    catch (BadLocationException e)
    {
      return ICharacterScanner.EOF;
    }
  }

  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    this.document = p_document;
    this.offset = p_offset;
    this.length = p_length;
    this.limit = offset + length;
    this.sawOpen = false;
    this.sawPath = false;
  }

  private boolean lookingAt(int off, char[] match)
  {
    for (int i = 0; i < match.length; ++i)
    {
      if (nextChar(i + off) != match[i])
      {
        return false;
      }
    }
    return true;
  }
  

  private IDocument document;
  private int offset;
  private int length;
  private int tokenOffset;
  private int tokenLength;
  private int limit;

}
