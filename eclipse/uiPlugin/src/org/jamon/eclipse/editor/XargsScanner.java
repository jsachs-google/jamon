package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class XargsScanner extends AbstractJavaContainingScanner
{
  public XargsScanner()
  {
    super("<%xargs>", "</%xargs>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(64, 64, 224)))));
  }  
}
