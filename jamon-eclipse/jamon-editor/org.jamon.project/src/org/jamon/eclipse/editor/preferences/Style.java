/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.graphics.RGB;

public final class Style
{
    private Set<StyleOption> m_options = EnumSet.noneOf(StyleOption.class);
    private RGB m_foreground = new RGB(0, 0, 0);
    private RGB m_background = null;

    public void setOption(StyleOption styleOption, boolean value) {
        if (value)
        {
            m_options.add(styleOption);
        }
        else
        {
            m_options.remove(styleOption);
        }
    }

    public boolean isOptionSet(StyleOption p_option)
    {
        return m_options.contains(p_option);
    }

    public int getSwtStyle()
    {
        int style = 0;
        for (StyleOption styleOption: StyleOption.values())
        {
            if (m_options.contains(styleOption))
            {
                style |= styleOption.getSwtStyleConstant();
            }
        }
        return style;
    }

    public RGB getForeground() { return m_foreground; }
    public void setForeground(RGB p_foreground)
    {
        assertNotNull(p_foreground);
        m_foreground = p_foreground;
    }

    public RGB getBackground() { return m_background; }
    public void setBackground(RGB p_background)
    {
        m_background = p_background;
    }

    private void assertNotNull(RGB p_rgb)
    {
        if (null == p_rgb)
        {
            throw new IllegalArgumentException("color may not be null");
        }
    }

    public void setSwtStyle(int p_swtStyle)
    {
        for (StyleOption option: StyleOption.values())
        {
            setOption(option, (p_swtStyle & option.getSwtStyleConstant()) != 0);
        }
    }

    @Override public boolean equals(Object p_other)
    {
        if (this == p_other)
        {
            return true;
        }
        if (p_other instanceof Style)
        {
            Style style = (Style)p_other;
            return ((style.m_background == null && m_background == null) 
                    || (style.m_background != null && style.m_background.equals(m_background)))
                && style.m_foreground.equals(m_foreground)
                && style.m_options.equals(m_options);
        }
        else
        {
            return false;
        }
    }

    @Override public int hashCode()
    {
        return
            (((m_options.hashCode() * 31)) + m_foreground.hashCode() * 31) + (m_background == null ? 0 : m_background.hashCode());
    }

    @Override public String toString()
    {
        return "options: " + m_options
            + "; foreground: " + m_foreground
            + "; background: " + m_background;
    }
}