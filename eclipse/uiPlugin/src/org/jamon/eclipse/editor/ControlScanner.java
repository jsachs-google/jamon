package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class ControlScanner extends AbstractJavaContainingScanner
{
  
  public ControlScanner(String open, String close, RGB fgColor, RGB bgColor) 
  {
    super(open, close, fgColor, bgColor);
  }
  

}
