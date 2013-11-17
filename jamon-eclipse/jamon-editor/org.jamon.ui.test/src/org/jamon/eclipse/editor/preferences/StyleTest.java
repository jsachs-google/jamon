/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import junit.framework.TestCase;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class StyleTest extends TestCase
{
    private Style style;
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        style = new Style();
    }

    public void testSetOption()
    {
        style.setOption(StyleOption.BOLD, true);
        assertTrue(style.isOptionSet(StyleOption.BOLD));
        style.setOption(StyleOption.BOLD, false);
        assertFalse(style.isOptionSet(StyleOption.BOLD));
    }

    public void testSwtStyle()
    {
        int swtStyle = SWT.BOLD | TextAttribute.STRIKETHROUGH;
        style.setSwtStyle(swtStyle);
        assertEquals(swtStyle, style.getSwtStyle());
        assertTrue(style.isOptionSet(StyleOption.BOLD));
        assertTrue(style.isOptionSet(StyleOption.STRIKETHROUGH));
        assertFalse(style.isOptionSet(StyleOption.UNDERLINE));
        assertFalse(style.isOptionSet(StyleOption.ITALIC));
        assertEquals(swtStyle, style.getSwtStyle());
    }
    
    public void testDefaultBg()
    {
        style = new Style();
        assertEquals(style, new Style());
        style.setForeground(new RGB(4,5,6));
        Style s2 = new Style();
        s2.setForeground(new RGB(4,5,6));
        assertEquals(style, s2);
        s2.setForeground(new RGB(3,5,6));
        assertFalse(style.equals(s2));
        s2.setForeground(new RGB(4,5,6));
        s2.setBackground(new RGB(0,1,2));
        assertFalse(style.equals(s2));
        style.setBackground(new RGB(0,1,2));
        assertEquals(style, s2);
    }
        
}
