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
    checkToken(1, CallScanner.DEFAULT);
    checkToken(2, CallScanner.TAG);
    checkDone();
  }
  
  public void testWithArgs()
  {
    setDocument("<& /a/b/c : x => 3; y => \"yes\" &>");
    checkToken(2, CallScanner.TAG);
    checkToken(1, Token.WHITESPACE);
    checkToken(6, CallScanner.PATH);
    checkToken(22, CallScanner.DEFAULT);
    checkToken(2, CallScanner.TAG);
    checkDone();
  }
  
  public void testPartial()
  {
    setDocument("<& /a/b/c  : x => 3 &");
    checkToken(2, CallScanner.TAG);
    checkToken(1, Token.WHITESPACE);
    checkToken(6, CallScanner.PATH);
    checkToken(12, CallScanner.DEFAULT);
    checkDone();
  }

}
