package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class JavaScanner extends AbstractJavaContainingScanner
{
  public JavaScanner()
  {
    super("<%java>", "</%java>", new RGB(32, 127, 127), new RGB(240, 252, 232));
  }  
}
