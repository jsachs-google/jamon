package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class SimpleBgScanner extends BufferedRuleBasedScanner
{
  public SimpleBgScanner(String propertyBase)
  {
    final JamonColorProvider provider = JamonColorProvider.instance();
    final IToken defaultToken =
      new Token(new TextAttribute(provider.getColor(SimpleScanner.fg(propertyBase)), provider.getColor(SimpleScanner.bg(propertyBase)), SWT.NORMAL));
    setDefaultReturnToken(defaultToken);
  }

}
