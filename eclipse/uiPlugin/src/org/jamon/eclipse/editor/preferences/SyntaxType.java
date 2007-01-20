package org.jamon.eclipse.editor.preferences;

import java.util.HashMap;
import java.util.Map;

    public enum SyntaxType {
        ARGS("Args", "args"),
        XARGS("Xargs", "xargs"),
        JAVA("Java code", "java"),
        ALIAS("Alias", "alias"),
        CLASS("Class", "class"),
        IMPORT("Imports", "imports"),
        CALL_CONTENT("Call content", "callContent"),
        CALL("Call", "call"),
        FLOW_CONTROL("Flow control", "flowControl"),
        DEF("Def", "def"),
        METHOD("Method", "method"),
        EMIT("Emit", "emit"),
        DOC("Doc", "doc");

    private SyntaxType(String p_label, String p_preferenceKey)
    {

        m_label = p_label;
        m_stylePreferenceKey =
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.STYLE_PREF_KEY;
        m_foregroundPreferenceKey=
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.FOREGROUND_PREF_KEY;
        m_backgroundPreferenceKey =
            SyntaxPreferences.SYNTAX_PREF_BASE + p_preferenceKey + SyntaxPreferences.BACKGROUND_PREF_KEY;

    }

    public String getLabel() { return m_label; }
    public String getStylePreferenceKey() { return m_stylePreferenceKey; }
    public String getForegroundPreferenceKey() { return m_foregroundPreferenceKey; }
    public String getBackgroundPreferenceKey() { return m_backgroundPreferenceKey; }

    public static SyntaxType getByPreferenceKey(String p_preferencekey)
    {
        return s_typesByPreferenceKey.get(p_preferencekey);
    }

    private final String m_label;
    private final String m_stylePreferenceKey;
    private final String m_foregroundPreferenceKey;
    private final String m_backgroundPreferenceKey;

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