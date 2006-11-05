package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class DocScanner implements ITokenScanner
{
  public int getTokenLength()
  {
    return tokenLength;
  }
  
  public int getTokenOffset()
  {
    return tokenOffset;
  }
  
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

  private boolean lookingAt(int off, char[] match)
  {
    for (int i = 0; i < match.length; ++i)
    {
      if (charAt(i + off) != match[i])
      {
        return false;
      }
    }
    return true;
  }

  private int charAt(int i)
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



  static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 127, 127)), null, SWT.BOLD));
  static final IToken DOC = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(112, 112, 112)), null, SWT.ITALIC));

  
  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    this.document = p_document;
    this.initialOffset = p_offset;
    this.offset = p_offset;
    this.length = p_length;
    this.limit = offset + length;
  }

  @SuppressWarnings("unused")
  private IDocument document;
  private int offset;
  private int initialOffset;
  private int length;
  private int tokenOffset;
  private int tokenLength;
  private int limit;
}
