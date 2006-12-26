package org.jamon.eclipse.editor;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JamonWhitespaceRule implements IRule {
  public IToken evaluate(ICharacterScanner scanner)
  {
    int c= scanner.read();
    if (Character.isWhitespace((char) c)) {
      do {
        c= scanner.read();
      } while (Character.isWhitespace((char) c));
      scanner.unread();
      return token;
    }

    scanner.unread();
    return Token.UNDEFINED;
  }
  
  public JamonWhitespaceRule(IToken wstoken) {
    token = wstoken;
  }
  
  private final IToken token;
}
