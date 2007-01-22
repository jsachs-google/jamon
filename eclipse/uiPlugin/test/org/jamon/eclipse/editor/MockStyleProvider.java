package org.jamon.eclipse.editor;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jamon.eclipse.editor.preferences.Style;
import org.jamon.eclipse.editor.preferences.StyleOption;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public class MockStyleProvider implements StyleProvider
{
    private final Map<SyntaxType, Set<SyntaxStyleChangeListener>> m_listeners =
        new EnumMap<SyntaxType, Set<SyntaxStyleChangeListener>>(SyntaxType.class);

    private final Map<SyntaxType, Style> m_styles = new EnumMap<SyntaxType, Style>(SyntaxType.class);

    public void addStylesChangedListener(StylesChangeListener p_listener)
    {
        throw new UnsupportedOperationException();
    }

    public void addSyntaxStyleChangeListener(SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener)
    {
        if (m_listeners.get(p_syntaxType) == null)
        {
            m_listeners.put(p_syntaxType, new HashSet<SyntaxStyleChangeListener>());
        }
        m_listeners.get(p_syntaxType).add(p_listener);
    }

    public Style getStyle(SyntaxType p_syntaxType)
    {
        return m_styles.get(p_syntaxType);
    }

    public void setStyle(SyntaxType p_syntaxType, boolean p_italic, boolean p_bold)
    {
        Style style = new Style();
        style.setOption(StyleOption.BOLD, p_bold);
        style.setOption(StyleOption.ITALIC, p_italic);
        m_styles.put(p_syntaxType, style);
    }

    public void reloadStyles()
    {
        throw new UnsupportedOperationException();
    }

    public void removeStylesChangedListener(StylesChangeListener p_listener)
    {
        throw new UnsupportedOperationException();
    }

    public void removeSyntaxStyleChangeListener(
        SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener)
    {
        m_listeners.get(p_syntaxType).remove(p_listener);
    }

    public boolean containsListener(SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener)
    {
        Set<SyntaxStyleChangeListener> listeners = m_listeners.get(p_syntaxType);
        return m_listeners != null && listeners.contains(p_listener);
    }
}
