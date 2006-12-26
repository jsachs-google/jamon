package org.jamon.eclipse.editor;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public abstract class AbstractScannerTest extends TestCase
{
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    JamonColorProvider.setColorFactory(new MockColorFactory());
    m_scanner = makeScanner();
    offset = 0;
  }
  
  protected abstract ITokenScanner makeScanner();

  protected void setDocument(String content)
  {
    MockDocument doc = new MockDocument(content);
    m_scanner.setRange(doc, 0, doc.getLength());
  }
  
  private ITokenScanner m_scanner;

  private int offset;

  protected void skipNTokens(int numToSkip) 
  {
    for (int i = 0; i < numToSkip; i++)
    {
      if (m_scanner.nextToken().isEOF())
      {
        Assert.fail("Hit EOF at token " + i);
      }
      offset += m_scanner.getTokenLength();
    }
  }
  
  protected void checkToken(int length, IToken expected)
  {
    IToken tok = m_scanner.nextToken();
    if (expected != tok)
    {
      Assert.fail("Expected " + expected.getData()  + " token, but got " + tok.getData());
    }
    assertEquals(length, m_scanner.getTokenLength());
    assertEquals(offset, m_scanner.getTokenOffset());
    offset += length;
  }

  protected void checkDone()
  {
    assertEquals(Token.EOF, m_scanner.nextToken());
  }


}
