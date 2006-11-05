package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
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

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    if (! sawOpen)
    {
      tokenOffset = offset;
      tokenLength = "<%doc>".length();
      offset += tokenLength;
      sawOpen = true;
      return TAG;
    }
    else if (! sawDoc)
    {
      tokenOffset = offset;
      offset = limit - "</%doc>".length(); 
      tokenLength = offset - tokenOffset;
      sawDoc = true;
      return DOC;
    }
    else
    {
      tokenOffset = offset;
      offset = limit;
      tokenLength = "</%doc>".length();
      return TAG;
    }
  }
  
  private static final IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 127, 127)), null, SWT.BOLD));
  private static final IToken DOC = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(112, 112, 112)), null, SWT.ITALIC));

  
  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    this.document = p_document;
    this.offset = p_offset;
    this.length = p_length;
    this.limit = offset + length;
    this.sawDoc = false;
    this.sawOpen = false;
  }

  @SuppressWarnings("unused")
  private IDocument document;
  private int offset;
  private int length;
  private int tokenOffset;
  private int tokenLength;
  private int limit;
  private boolean sawOpen;
  private boolean sawDoc;
}
