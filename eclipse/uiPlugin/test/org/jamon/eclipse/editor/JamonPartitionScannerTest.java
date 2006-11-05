package org.jamon.eclipse.editor;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class JamonPartitionScannerTest extends TestCase
{
  
  private static final class MockColorFactory implements JamonColorProvider.ColorFactory
  {
    public Color colorFor(RGB p_rgb)
    {
      return null;
    }
  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    JamonColorProvider.setColorFactory(new MockColorFactory());
    m_scanner = new JamonPartitionScanner();
  }
  
  private JamonPartitionScanner m_scanner;
  
  public void testNoMarkup()
  {
    MockDocument doc = new MockDocument("Just some plain old <b>text</b> but not Jamon.\nAdd a second lline though.\n");
    m_scanner.setRange(doc, 0, doc.getLength());
    assertEquals(JamonPartitionScanner.JAMON_TOKEN, m_scanner.nextToken());
    assertEquals(0, m_scanner.getTokenOffset());
    assertEquals(doc.getLength(), m_scanner.getTokenLength());
    assertEquals(Token.EOF, m_scanner.nextToken());
  }
}
