package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class JavaScanner extends AbstractJavaContainingScanner
{
  public JavaScanner()
  {
    super("<%java>", "</%java>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(32, 127, 127)))));
  }  
}
