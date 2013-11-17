/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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