package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class XargsScanner extends AbstractJavaContainingScanner
{
  public XargsScanner()
  {
    super("<%xargs>", "</%xargs>", new RGB(64, 64, 224), new RGB(255, 255, 244));
  }  
}
