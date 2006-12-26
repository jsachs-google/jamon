package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class ImportScanner extends AbstractJavaContainingScanner
{
  public ImportScanner()
  {
    super("<%import>", "</%import>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(64, 224, 64)))));
  }  
}
