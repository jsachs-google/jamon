/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
        m_scanner.dispose();
        assertFalse(m_styleProvider.containsListener(
            SyntaxType.DOC_TAG, (SyntaxStyleChangeListener) m_scanner));
        assertFalse(m_styleProvider.containsListener(
            SyntaxType.DOC_BODY, (SyntaxStyleChangeListener) m_scanner));
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
