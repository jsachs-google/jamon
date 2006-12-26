package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class ClassScanner extends AbstractJavaContainingScanner
{
  public ClassScanner()
  {
    super("<%class>", "</%class>", new RGB(32, 127, 127), new RGB(224, 255, 224));
  }  
}
