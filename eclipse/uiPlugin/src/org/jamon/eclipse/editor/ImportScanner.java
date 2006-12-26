package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class ImportScanner extends AbstractJavaContainingScanner
{
  public ImportScanner()
  {
    super("<%import>", "</%import>", new RGB(64, 224, 64), new RGB(224, 255, 224));
  }  
}
