/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
    }

    public void testAddListener()
    {
        setScanner(0);
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
    setScanner(0);
    setDocument("<& /a/b/c &>");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(1);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testSimpleWithContent()
  {
    setScanner(1);
    setDocument("<&| /a/b/c &>");
    checkToken(3, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(1);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testSimpleWithMultiContent()
  {
    setScanner(2);
    setDocument("<&|| /a/b/c &>");
    checkToken(4, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(1);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testWithArgs()
  {
    setScanner(0);
    setDocument("<& /a/b/c : x => 3; y = \"yes\" &>");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(17);
    checkToken(2, SyntaxType.CALL);
    checkDone();
  }

  public void testWithArgsAndContent()
  {
      setScanner(1);
      setDocument("<&| /a/b/c : x => 3; y = \"yes\" &>");
      checkToken(3, SyntaxType.CALL);
      skipNTokens(1); //checkToken(1, CallScanner.WHITESPACE);
      checkToken(6, SyntaxType.CALL_PATH);
      skipNTokens(17);
      checkToken(2, SyntaxType.CALL);
      checkDone();
  }

  public void testPartial()
  {
    setScanner(0);
    setDocument("<& /a/b/c  : x => 3 &");
    checkToken(2, SyntaxType.CALL);
    skipNTokens(1); // checkToken(1, CallScanner.WHITESPACE);
    checkToken(6, SyntaxType.CALL_PATH);
    skipNTokens(9);
    checkDone();
  }

  private void setScanner(int pipeCount)
  {
    StringBuilder openTag = new StringBuilder("<&");
    for (int i = 0; i < pipeCount; i++) {
      openTag.append('|');
    }
    m_scanner = new CallScanner(m_styleProvider, openTag.toString(), "&>");
  }
}
