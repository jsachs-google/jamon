package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public class CallScannerTest extends AbstractScannerTest
{
  
  @Override
  protected ITokenScanner makeScanner()
  {
    return new CallScanner();
  }

  public void testSimple()
  {
    setDocument("<& /a/b/c &>");
    checkToken(2, CallScanner.TAG);
    checkToken(1, Token.WHITESPACE);
    checkToken(6, CallScanner.PATH);
    checkToken(1, Token.WHITESPACE);
    checkToken(2, CallScanner.TAG);
    checkDone();
  }

}
