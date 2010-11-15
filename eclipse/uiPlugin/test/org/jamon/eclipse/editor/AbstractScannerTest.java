package org.jamon.eclipse.editor;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.Style;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public abstract class AbstractScannerTest extends TestCase
{
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    JamonColorProvider.setColorFactory(new MockColorFactory());
    offset = 0;
    m_styleProvider = new MockStyleProvider();
  }

  protected void setDocument(final String content)
  {
    IDocument doc = DocumentMocker.createMockDocument(content);
    m_scanner.setRange(doc, 0, doc.getLength());
  }

  protected DisposableScanner m_scanner;
  protected MockStyleProvider m_styleProvider;

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

  protected void checkToken(int length, SyntaxType p_expectedType)
  {
    IToken token = m_scanner.nextToken();
    assertEquals(length, m_scanner.getTokenLength());
    assertEquals(offset, m_scanner.getTokenOffset());
    offset += length;
    Object tokenData = token.getData();
    if (tokenData instanceof TextAttribute) {
        TextAttribute textAttribute = (TextAttribute)tokenData;
        // Since we cannot construct Colors in unit tests, we'll have to satisfy ourselves with
        // checking the style.
        Style style = m_styleProvider.getStyle(p_expectedType);
        assertEquals(style.getSwtStyle(), textAttribute.getStyle());
    }
    else
    {
        fail("token did not contain a text attribute");
    }
  }

  protected void checkDone()
  {
    assertEquals(Token.EOF, m_scanner.nextToken());
  }


}
