package org.jamon.eclipse.editor.preferences;

import java.util.EnumMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.JamonProjectPlugin;

public class SyntaxPreferences
{
    final static String SYNTAX_PREF_BASE = "org.jamon.syntax.";
    final static String FOREGROUND_PREF_KEY = ".foreground";
    final static String BACKGROUND_PREF_KEY = ".background";
    final static String STYLE_PREF_KEY = ".style";

    private final Map<SyntaxType, Style> m_syntaxTypeStyles =
        new EnumMap<SyntaxType, Style>(SyntaxType.class);
    private static IPreferenceStore s_preferenceStore;

    public static Style loadStyle(SyntaxType p_syntaxType)
    {
        Style style = new Style();
        style.setSwtStyle(getPreferenceStore().getInt(p_syntaxType.getStylePreferenceKey()));
        style.setForeground(stringToRgb(
            getPreferenceStore().getString(p_syntaxType.getForegroundPreferenceKey())));
        style.setBackground(stringToRgb(
            getPreferenceStore().getString(p_syntaxType.getBackgroundPreferenceKey())));
        return style;
    }

    private static Style defaultStyle(SyntaxType p_syntaxType)
    {
        Style style = new Style();
        style.setSwtStyle(getPreferenceStore().getDefaultInt(p_syntaxType.getStylePreferenceKey()));
        style.setForeground(stringToRgb(
            getPreferenceStore().getDefaultString(p_syntaxType.getForegroundPreferenceKey())));
        style.setBackground(stringToRgb(
            getPreferenceStore().getDefaultString(p_syntaxType.getBackgroundPreferenceKey())));
        return style;
    }
    
    /**
     * Apply changes to the underlying preferences object.
     */
    public void apply()
    {
        final IPreferenceStore store = getPreferenceStore();
        for (SyntaxType syntaxType: m_syntaxTypeStyles.keySet())
        {
            final Style style = m_syntaxTypeStyles.get(syntaxType);
            final Style storedStyle = loadStyle(syntaxType);
            final Style defaultStyle = defaultStyle(syntaxType);
            if (style.getSwtStyle() == defaultStyle.getSwtStyle())
            {
                store.setToDefault(syntaxType.getStylePreferenceKey());
            }
            else if (style.getSwtStyle() != storedStyle.getSwtStyle())
            {
                store.setValue(syntaxType.getStylePreferenceKey(), style.getSwtStyle());
            }
            
            if (style.getBackground().equals(defaultStyle.getBackground()))
            {
                store.setToDefault(syntaxType.getBackgroundPreferenceKey());
            }
            else if (! style.getBackground().equals(storedStyle.getBackground()))
            {
                store.setValue(syntaxType.getBackgroundPreferenceKey(), rgbToString(style.getBackground()));
            }
            
            if (style.getForeground().equals(defaultStyle.getForeground()))
            {
                store.setToDefault(syntaxType.getForegroundPreferenceKey());
            }
            else if (! style.getForeground().equals(storedStyle.getForeground()))
            {
                store.setValue(syntaxType.getForegroundPreferenceKey(), rgbToString(style.getForeground()));
            }
        }
        //FIXME - avoid cyclic dependencies between packages
        PreferencesStyleProvider.getProvider().reloadStyles();
    }
    
    // package scoped for unit testing.
    static RGB stringToRgb(String p_string) {
        if (p_string == null || p_string.length() == 0) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(p_string, ",");
        int[] colorComponents = new int[3];
        for (int i = 0; i < colorComponents.length; i++)
        {
            colorComponents[i] = Integer.parseInt(tokenizer.nextToken());
        }
        return new RGB(colorComponents[0], colorComponents[1], colorComponents[2]);
    }

    // package scoped for unit testing.
    static String rgbToString(RGB p_rgb)
    {
        if (p_rgb == null)
        {
            return "";
        }
        return p_rgb.red + "," + p_rgb.green + "," + p_rgb.blue;
    }

    public Style getStyle(SyntaxType p_syntaxType)
    {
        Style style = m_syntaxTypeStyles.get(p_syntaxType);
        if (style == null) {
            style = loadStyle(p_syntaxType);
            m_syntaxTypeStyles.put(p_syntaxType, style);
        }
        return style;
    }

    public void setStyle(SyntaxType p_syntaxType, Style p_style)
    {
        m_syntaxTypeStyles.put(p_syntaxType, p_style);
    }

    public void restoreDefaults()
    {
        for (SyntaxType syntaxType: SyntaxType.values())
        {
            Style style = new Style();
            style.setSwtStyle(getPreferenceStore().getDefaultInt(syntaxType.getStylePreferenceKey()));
            style.setForeground(stringToRgb(
                getPreferenceStore().getDefaultString(syntaxType.getForegroundPreferenceKey())));
            style.setBackground(stringToRgb(
                getPreferenceStore().getDefaultString(syntaxType.getBackgroundPreferenceKey())));
            m_syntaxTypeStyles.put(syntaxType, style);
        }
    }

    public static IPreferenceStore getPreferenceStore()
    {
        if (s_preferenceStore == null)
        {
            s_preferenceStore = JamonProjectPlugin.getDefault().getPreferenceStore();
        }
        return s_preferenceStore;
    }
}
