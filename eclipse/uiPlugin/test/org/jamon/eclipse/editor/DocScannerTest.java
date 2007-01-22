package org.jamon.eclipse.editor;

import org.jamon.eclipse.editor.preferences.SyntaxType;
import org.jamon.eclipse.editor.preferences.StyleProvider.SyntaxStyleChangeListener;

public class DocScannerTest extends AbstractScannerTest
{
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        m_styleProvider.setStyle(SyntaxType.DOC_TAG, true, false);
        m_styleProvider.setStyle(SyntaxType.DOC_BODY, false, true);
        m_scanner = new DocScanner(m_styleProvider, "<%doc>", "</%doc>");
    }

    public void testAddListener()
    {
        assertTrue(m_styleProvider.containsListener(
            SyntaxType.DOC_BODY, (SyntaxStyleChangeListener) m_scanner));
        assertTrue(m_styleProvider.containsListener(
            SyntaxType.DOC_TAG, (SyntaxStyleChangeListener) m_scanner));
    }


  public void testFull()
  {
    setDocument("<%doc>this is a complete doc section\n   </%doc>");
    checkToken(6, SyntaxType.DOC_TAG);
    checkToken(34, SyntaxType.DOC_BODY);
    checkToken(7, SyntaxType.DOC_TAG);
    checkDone();
  }

  public void testPartial()
  {
    setDocument("<%doc>this is not a complete doc section\n   </%doc");
    checkToken(6, SyntaxType.DOC_TAG);
    checkToken(44, SyntaxType.DOC_BODY);
    checkDone();
  }

  public void testMinimal()
  {
    setDocument("<%doc>");
    checkToken(6, SyntaxType.DOC_TAG);
    checkDone();
  }
}
