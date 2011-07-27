/**
 *
 */
package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class MockColorFactory implements JamonColorProvider.ColorFactory
{
  @Override
  public Color colorFor(RGB p_rgb)
  {
    return null;
  }
}