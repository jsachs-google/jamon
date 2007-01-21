/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */
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
        PreferencesStyleProvider.getProvider().removeStylesChangedListener(m_stylesChangeListener);
        super.dispose();
    }
}
