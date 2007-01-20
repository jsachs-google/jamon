package org.jamon.eclipse.editor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.JamonProjectPlugin;

public class PreferencesInitializer extends AbstractPreferenceInitializer
{
    private static final String BLACK = SyntaxPreferences.rgbToString(new RGB(0,0,0));
    private static final String WHITE = SyntaxPreferences.rgbToString(new RGB(255, 255, 255));

    @Override public void initializeDefaultPreferences()
    {
        IPreferenceStore preferenceStore = JamonProjectPlugin.getDefault().getPreferenceStore();
        for (SyntaxType syntaxType : SyntaxType.values())
        {
            preferenceStore.setDefault(syntaxType.getStylePreferenceKey(), SWT.NONE);
            preferenceStore.setDefault(syntaxType.getBackgroundPreferenceKey(), WHITE);
            preferenceStore.setDefault(syntaxType.getForegroundPreferenceKey(), BLACK);
        }
    }

}
