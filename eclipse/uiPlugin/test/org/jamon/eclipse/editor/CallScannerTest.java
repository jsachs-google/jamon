package org.jamon.eclipse.editor;

import org.jamon.eclipse.editor.preferences.SyntaxType;
import org.jamon.eclipse.editor.preferences.StyleProvider.SyntaxStyleChangeListener;

public class CallScannerTest extends AbstractScannerTest
{
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        m_styleProvider.setStyle(SyntaxType.CALL, true, false);
        m_styleProvider.setStyle(SyntaxType.CALL_PATH, false, true);
        m_scanner = new CallScanner(m_styleProvider, "<&", "&>");
    }

    public void testAddListener()
    {
        assertTrue(m_styleProvider.containsListener(
            SyntaxType.CALL, (SyntaxStyleChangeListener) m_scanner));
        assertTrue(m_styleProvider.containsListener(
            SyntaxType.CALL_PATH, (SyntaxStyleChangeListener) m_scanner));
        m_scanner.dispose();
        assertFalse(m_styleProvider.containsListener(
            SyntaxType.CALL, (SyntaxStyleChangeListener) m_scanner));
        assertFalse(m_styleProvider.containsListener(
            SyntaxType.CALL_PATH, (SyntaxStyleChangeListener) m_scanner));
    }


  public void testSimple()
  {
    setDocument("<& /a/b/c &>");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(1);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testWithArgs()
  {
    setDocument("<& /a/b/c : x => 3; y = \"yes\" &>");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(17);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testPartial()
  {
    setDocument("<& /a/b/c  : x => 3 &");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); // checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(9);
    checkDone();
  }

}
