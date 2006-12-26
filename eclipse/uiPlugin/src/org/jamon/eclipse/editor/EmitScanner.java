package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class EmitScanner extends AbstractJavaContainingScanner
{
  public EmitScanner()
  {
    super("<%", "%>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 64, 64)))));
  }  
}
