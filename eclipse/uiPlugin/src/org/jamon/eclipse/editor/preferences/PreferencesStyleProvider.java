package org.jamon.eclipse.editor.preferences;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;

public class PreferencesStyleProvider implements StyleProvider
{
    private final static PreferencesStyleProvider s_singleton = new PreferencesStyleProvider();
    private final ListenerList m_stylesChangedListeners = new ListenerList(ListenerList.IDENTITY);
    private final Map<SyntaxType, ListenerList> m_syntaxStyleChangedListeners
       = new EnumMap<SyntaxType, ListenerList>(SyntaxType.class);
    private final Map<SyntaxType, Style> m_styles =
        new EnumMap<SyntaxType, Style>(SyntaxType.class);

    private PreferencesStyleProvider() {
        for (SyntaxType syntaxType: SyntaxType.values())
        {
            m_styles.put(syntaxType, SyntaxPreferences.loadStyle(syntaxType));
            m_syntaxStyleChangedListeners.put(syntaxType, new ListenerList(ListenerList.IDENTITY));
        }
    }

    public static StyleProvider getProvider()
    {
        return s_singleton;
    }

    public Style getStyle(SyntaxType p_syntaxType)
    {
        return m_styles.get(p_syntaxType);
    }

    public synchronized void reloadStyles()
    {
        for (Map.Entry<SyntaxType, Style> entry: m_styles.entrySet())
        {
            Style newStyle = SyntaxPreferences.loadStyle(entry.getKey());
            if (! newStyle.equals(entry.getValue()))
            {
                entry.setValue(newStyle);
                for (Object listener: m_syntaxStyleChangedListeners.get(entry.getKey()).getListeners())
                {
                    if (listener instanceof SyntaxStyleChangeListener) {
                        ((SyntaxStyleChangeListener)listener).styleChanged();
                    }
                }
            }
        }
        for (Object listener: m_stylesChangedListeners.getListeners())
        {
            if (listener instanceof StylesChangeListener)
            {
                ((StylesChangeListener) listener).stylesChanged();
            }
        }
    }

    public void addStylesChangedListener(StylesChangeListener p_listener)
    {
        m_stylesChangedListeners.add(p_listener);
    }

    public void addSyntaxStyleChangeListener(
        SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener)
    {
        m_syntaxStyleChangedListeners.get(p_syntaxType).add(p_listener);

    }

    public void removeStylesChangedListener(StylesChangeListener p_listener)
    {
        m_stylesChangedListeners.remove(p_listener);
    }

    public void removeSyntaxStyleChangeListener(
        SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener)
    {
        m_syntaxStyleChangedListeners.get(p_syntaxType).remove(p_listener);
    }
}
