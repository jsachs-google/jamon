/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import org.eclipse.swt.graphics.RGB;

import junit.framework.TestCase;

public class SyntaxPreferencesTest extends TestCase
{
    private static final String STRING_REP = "21,186,49";
    private static final RGB RGB_REP = new RGB(21,186,49);

    public void testStringToRgb()
    {
        assertEquals(RGB_REP, SyntaxPreferences.stringToRgb(STRING_REP));
    }

    public void testRgbToString()
    {
        assertEquals(STRING_REP, SyntaxPreferences.rgbToString(RGB_REP));
    }
}
