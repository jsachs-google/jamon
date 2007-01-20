package org.jamon.eclipse.editor.preferences;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;

import junit.framework.TestCase;

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
}
