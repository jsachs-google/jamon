/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

    public enum SyntaxType {
        ARGS("Args", "args", SWT.NONE, gray(0), gray(229)),
        XARGS("Xargs", "xargs", SWT.NONE, gray(0), gray(255)),
        JAVA("Java code", "java", SWT.BOLD, gray(0), gray(240)),
        ALIAS("Alias", "alias", SWT.NONE, gray(0), gray(255)),
        CLASS("Class", "class", SWT.NONE, gray(0), gray(255)),
        IMPORT("Imports", "imports", SWT.NONE, gray(0), gray(191)),
        CALL("Call", "call", SWT.BOLD, gray(0), gray(255)),
        CALL_PATH("Call path", "callPath", SWT.ITALIC, gray(0), gray(255)),
        FLOW_CONTROL("Flow control", "flowControl", SWT.NONE, gray(0), new RGB(255, 255, 0)),
        DEF("Def", "def", SWT.NONE, gray(0), new RGB(230, 230, 250)),
        METHOD("Method", "method", SWT.NONE, gray(0), gray(255)),
        EMIT("Emit", "emit", SWT.BOLD, gray(0), new RGB(191, 242, 191)),
        DOC_TAG("Doc Tag", "docTag", SWT.BOLD, gray(127), gray(255)),
        DOC_BODY("Doc Body", "docBody", SWT.ITALIC, gray(112), gray(255));

    private SyntaxType(
        String p_label,
        String p_preferenceKey,
        int p_defaultSwtStyle,
        RGB p_defaultForeground,
        RGB p_defaultBackground)
    {
        m_label = p_label;
        m_stylePreferenceKey =
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.STYLE_PREF_KEY;
        m_foregroundPreferenceKey=
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.FOREGROUND_PREF_KEY;
        m_backgroundPreferenceKey =
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.BACKGROUND_PREF_KEY;
        m_defaultSwtStyle = p_defaultSwtStyle;
        m_defaultForeground = p_defaultForeground;
        m_defaultBackground = p_defaultBackground;
    }

    private static RGB gray(int p_brightness)
    {
        return new RGB(p_brightness, p_brightness, p_brightness);
    }

    public String getLabel() { return m_label; }
    public String getStylePreferenceKey() { return m_stylePreferenceKey; }
    public String getForegroundPreferenceKey() { return m_foregroundPreferenceKey; }
    public String getBackgroundPreferenceKey() { return m_backgroundPreferenceKey; }
    public int getDefaultSwtStyle() { return m_defaultSwtStyle; }
    public RGB getDefaultForeground() { return m_defaultForeground; }
    public RGB getDefaultBackground() { return m_defaultBackground; }

    public static SyntaxType getByPreferenceKey(String p_preferencekey)
    {
        return s_typesByPreferenceKey.get(p_preferencekey);
    }

    private final String m_label;
    private final String m_stylePreferenceKey;
    private final String m_foregroundPreferenceKey;
    private final String m_backgroundPreferenceKey;
    private final int m_defaultSwtStyle;
    private final RGB m_defaultForeground, m_defaultBackground;

    private final static Map<String, SyntaxType> s_typesByPreferenceKey =
        new HashMap<String, SyntaxType>();

    static
    {
        for (SyntaxType syntaxType: values())
        {
            s_typesByPreferenceKey.put(syntaxType.m_backgroundPreferenceKey, syntaxType);
            s_typesByPreferenceKey.put(syntaxType.m_foregroundPreferenceKey, syntaxType);
            s_typesByPreferenceKey.put(syntaxType.m_stylePreferenceKey, syntaxType);
        }
    }

    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int i = 0;
        for (SyntaxType syntaxType : values()) {
            names[i++] = syntaxType.getLabel();
        }
        return names;
    }
}