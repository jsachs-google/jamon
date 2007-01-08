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

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.themes.IThemeManager;
import org.jamon.eclipse.editor.JamonEditorSourceViewerConfiguration;
import org.jamon.eclipse.editor.SimpleScanner;

public class JamonEditor extends AbstractDecoratedTextEditor
{
    private static final String EDITOR_CONTEXT_MENU_ID =
        JamonProjectPlugin.getDefault().pluginId() + ".editorContext";
    private IPropertyChangeListener m_propertyChangeListener;

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
            m_propertyChangeListener = new IPropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event)
                {
                    if (event.getProperty().startsWith(SimpleScanner.THEME_BASE)) {
                        jamonEditorSourceViewerConfiguration.setDamageRepairers();
                        getSourceViewer().setDocument(getDocumentProvider().getDocument(getEditorInput()));
                    }
                }
            };
            getThemeManager().addPropertyChangeListener(m_propertyChangeListener);
        }
    }

    @Override public void dispose()
    {
        getThemeManager().removePropertyChangeListener(m_propertyChangeListener);
        super.dispose();
    }

    private IThemeManager getThemeManager()
    {
        return PlatformUI.getWorkbench().getThemeManager();
    }

}
