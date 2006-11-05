package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.ITokenScanner;

public abstract class AbstractScanner implements ITokenScanner
{
  public int getTokenLength()
  {
    return tokenLength;
  }
  
  public int getTokenOffset()
  {
    return tokenOffset;
  }
  
  protected boolean lookingAt(int off, char[] match)
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

  protected int charAt(int i)
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
    this.initialOffset = p_offset;
    this.offset = p_offset;
    this.length = p_length;
    this.limit = offset + length;
  }

  private IDocument document;
  protected int offset;
  protected int initialOffset;
  protected int length;
  protected int tokenOffset;
  protected int tokenLength;
  protected int limit;

}
