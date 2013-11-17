/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

public interface StyleProvider
{
    /**
     * A listener to record that some styles have changed.
     */
    public static interface StylesChangeListener
    {
        void stylesChanged();
    }

    /**
     * A listener to record that the style for a particular SyntaxType has changed.
     */
    public static interface SyntaxStyleChangeListener
    {
        void styleChanged();
    }

    Style getStyle(SyntaxType p_syntaxType);

    void addStylesChangedListener(StylesChangeListener p_listener);
    void removeStylesChangedListener(StylesChangeListener p_listener);
    void addSyntaxStyleChangeListener(
        SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener);
    void removeSyntaxStyleChangeListener(
        SyntaxType p_syntaxType, SyntaxStyleChangeListener p_listener);

    /**
     * Reload all styles, and then notify listeners.  First, all
     * {@link StyleProvider.SyntaxStyleChangeListener}s are notified, and then all
     * {@link StyleProvider.StylesChangeListener}s are notified.  The intention is that scanners
     * can update their various styles, and then editors can force a rescan.
     */
    void reloadStyles();
}
