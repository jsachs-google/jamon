package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.Token;

import junit.framework.TestCase;

public class DocScannerTest extends TestCase
{
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    JamonColorProvider.setColorFactory(new MockColorFactory());
    m_scanner = new DocScanner();
  }
  
  private DocScanner m_scanner;
  
  public void testFull()
  {
    MockDocument doc = new MockDocument("<%doc>this is a complete doc section\n   </%doc>");
    m_scanner.setRange(doc, 0, doc.getLength());
    assertEquals(DocScanner.TAG, m_scanner.nextToken());
    assertEquals(0, m_scanner.getTokenOffset());
    assertEquals(6, m_scanner.getTokenLength());
    assertEquals(DocScanner.DOC, m_scanner.nextToken());
    assertEquals(6, m_scanner.getTokenOffset());
    assertEquals(doc.getLength() - 13, m_scanner.getTokenLength());
    assertEquals(DocScanner.TAG, m_scanner.nextToken());
    assertEquals(doc.getLength() - 7, m_scanner.getTokenOffset());
    assertEquals(7, m_scanner.getTokenLength());
    assertEquals(Token.EOF, m_scanner.nextToken());
  }

  public void testPartial()
  {
    MockDocument doc = new MockDocument("<%doc>this is not a complete doc section\n   </%doc");
    m_scanner.setRange(doc, 0, doc.getLength());
    assertEquals(DocScanner.TAG, m_scanner.nextToken());
    assertEquals(0, m_scanner.getTokenOffset());
    assertEquals(6, m_scanner.getTokenLength());
    assertEquals(DocScanner.DOC, m_scanner.nextToken());
    assertEquals(6, m_scanner.getTokenOffset());
    assertEquals(doc.getLength() - 6, m_scanner.getTokenLength());
    assertEquals(Token.EOF, m_scanner.nextToken());
  }

  public void testMinimal()
  {
    MockDocument doc = new MockDocument("<%doc>");
    m_scanner.setRange(doc, 0, doc.getLength());
    assertEquals(DocScanner.TAG, m_scanner.nextToken());
    assertEquals(0, m_scanner.getTokenOffset());
    assertEquals(6, m_scanner.getTokenLength());
    assertEquals(Token.EOF, m_scanner.nextToken());
  }


}
