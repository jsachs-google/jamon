package org.jamon.eclipse.editor.preferences;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.EclipseUtils;
import org.jamon.eclipse.JamonProjectPlugin;

public class PreferencesInitializer extends AbstractPreferenceInitializer
{
    private static final String BLACK = SyntaxPreferences.rgbToString(new RGB(0,0,0));
    private static final String DEFAULT = "";
    
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
            preferenceStore.setDefault(syntaxType.getStylePreferenceKey(), SWT.NONE);
            preferenceStore.setDefault(syntaxType.getBackgroundPreferenceKey(), DEFAULT);
            preferenceStore.setDefault(syntaxType.getForegroundPreferenceKey(), BLACK);
        }

        preferenceStore.setDefault(SyntaxType.DOC_TAG.getStylePreferenceKey(), SWT.BOLD);
        preferenceStore.setDefault(
            SyntaxType.DOC_TAG.getForegroundPreferenceKey(),
            SyntaxPreferences.rgbToString(new RGB(127, 127, 127)));
        preferenceStore.setDefault(SyntaxType.DOC_BODY.getStylePreferenceKey(), SWT.ITALIC);
        preferenceStore.setDefault(
            SyntaxType.DOC_BODY.getForegroundPreferenceKey(),
            SyntaxPreferences.rgbToString(new RGB(112, 112, 112)));
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
