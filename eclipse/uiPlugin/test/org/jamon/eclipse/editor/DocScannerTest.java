package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.ITokenScanner;

public class DocScannerTest extends AbstractScannerTest
{
  @Override
  protected ITokenScanner makeScanner()
  {
    return new DocScanner(null, "<%doc>", "</%doc>");
  }

  public void testFull()
  {
    setDocument("<%doc>this is a complete doc section\n   </%doc>");
    checkToken(6, DocScanner.TAG);
    checkToken(34, DocScanner.DOC);
    checkToken(7, DocScanner.TAG);
    checkDone();
  }

  public void testPartial()
  {
    setDocument("<%doc>this is not a complete doc section\n   </%doc");
    checkToken(6, DocScanner.TAG);
    checkToken(44, DocScanner.DOC);
    checkDone();
  }

  public void testMinimal()
  {
    setDocument("<%doc>");
    checkToken(6, DocScanner.TAG);
    checkDone();
  }
}
