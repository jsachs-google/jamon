/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JamonWhitespaceRule implements IRule {
  @Override
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
