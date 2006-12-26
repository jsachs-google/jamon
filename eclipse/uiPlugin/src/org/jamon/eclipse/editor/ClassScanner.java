package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class ClassScanner extends AbstractJavaContainingScanner
{
  public ClassScanner()
  {
    super("<%class>", "</%class>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(32, 127, 127)))));
  }  
}
