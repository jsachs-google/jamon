/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;

public enum StyleOption
{
    ITALIC("italic", SWT.ITALIC),
    UNDERLINE("underline", TextAttribute.UNDERLINE),
    BOLD("bold", SWT.BOLD),
    STRIKETHROUGH("strikethrough", TextAttribute.STRIKETHROUGH);

    private StyleOption(String p_label, int p_swtStyleConstant)
    {
        m_label = p_label;
        m_swtStyleConstant = p_swtStyleConstant;
    }


    public String getLabel() { return m_label; }
    public int getSwtStyleConstant() { return m_swtStyleConstant; }


    private final String m_label;
    private final int m_swtStyleConstant;

}