package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class EmitScanner extends AbstractJavaContainingScanner
{
  public EmitScanner()
  {
    super("<%", "%>", new RGB(127, 64, 64), new RGB(240, 248, 240));
  }  
}
