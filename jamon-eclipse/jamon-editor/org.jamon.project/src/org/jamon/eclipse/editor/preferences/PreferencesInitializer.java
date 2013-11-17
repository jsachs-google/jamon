/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor.preferences;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.jamon.eclipse.EclipseUtils;
import org.jamon.eclipse.JamonProjectPlugin;

public class PreferencesInitializer extends AbstractPreferenceInitializer
{
    @Override public void initializeDefaultPreferences()
    {
        IPreferenceStore preferenceStore = JamonProjectPlugin.getDefault().getPreferenceStore();
        setDefaultDefaultPreferences(preferenceStore);
        loadFromStandardPreferences(preferenceStore);
    }

    private void setDefaultDefaultPreferences(IPreferenceStore preferenceStore)
    {
        for (SyntaxType syntaxType : SyntaxType.values())
        {
            preferenceStore.setDefault(
                syntaxType.getStylePreferenceKey(), syntaxType.getDefaultSwtStyle());
            preferenceStore.setDefault(
                syntaxType.getForegroundPreferenceKey(),
                SyntaxPreferences.rgbToString(syntaxType.getDefaultForeground()));
            preferenceStore.setDefault(
                syntaxType.getBackgroundPreferenceKey(),
                SyntaxPreferences.rgbToString(syntaxType.getDefaultBackground()));
        }
    }

    private void loadFromStandardPreferences(IPreferenceStore preferenceStore)
    {
        InputStream is = getClass().getResourceAsStream("jamon.syntax.default.preferences");
        if (is != null)
        {
            try
            {
                PreferenceStore ps = new PreferenceStore();
                ps.load(is);
                for (String name : ps.preferenceNames())
                {
                    preferenceStore.setDefault(name, ps.getString(name));
                }
            }
            catch (IOException e)
            {
                EclipseUtils.logError(e);
            }
            finally
            {
                EclipseUtils.closeQuiety(is);
            }
        }
    }

}
