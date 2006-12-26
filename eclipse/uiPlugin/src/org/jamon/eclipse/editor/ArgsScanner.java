package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public class ArgsScanner extends AbstractJavaContainingScanner
{
  public ArgsScanner()
  {
    super("<%args>", "</%args>", new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(64, 64, 224)))));
  }  
}
