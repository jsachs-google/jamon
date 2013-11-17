/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.jamon.eclipse.editor.JamonEditorSourceViewerConfiguration;
import org.jamon.eclipse.editor.preferences.PreferencesStyleProvider;
import org.jamon.eclipse.editor.preferences.StyleProvider.StylesChangeListener;

public class JamonEditor extends AbstractDecoratedTextEditor
{
    private static final String EDITOR_CONTEXT_MENU_ID =
        JamonProjectPlugin.getDefault().pluginId() + ".editorContext";
    private StylesChangeListener m_stylesChangeListener;

    public final static String JAMON_PARTITIONING =
        JamonProjectPlugin.getDefault().pluginId() + ".partitioning";

    public JamonEditor()
    {
        setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
    }

    private static boolean useEditor = true;

    @Override
    protected void initializeEditor()
    {
        super.initializeEditor();
        if (useEditor)
        {
            final JamonEditorSourceViewerConfiguration jamonEditorSourceViewerConfiguration =
                new JamonEditorSourceViewerConfiguration();
            setSourceViewerConfiguration(jamonEditorSourceViewerConfiguration);
            m_stylesChangeListener = new StylesChangeListener() {
                @Override
                public void stylesChanged()
                {
                    // force a reparse
                    getSourceViewer().setDocument(
                        getDocumentProvider().getDocument(getEditorInput()));
                }
            };
            PreferencesStyleProvider.getProvider().addStylesChangedListener(m_stylesChangeListener);
        }
    }

    @Override public void dispose()
    {
        //BROKEN - dispose of SyntaxStyleChangeListeners
        PreferencesStyleProvider.getProvider().removeStylesChangedListener(m_stylesChangeListener);
        super.dispose();
    }
}
