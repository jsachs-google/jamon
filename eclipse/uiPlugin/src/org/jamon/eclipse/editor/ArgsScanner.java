package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class ArgsScanner extends AbstractJavaContainingScanner
{
  public ArgsScanner()
  {
    super("<%args>", "</%args>", new RGB(64, 64, 224), new RGB(255, 240, 240));
  }  
}
