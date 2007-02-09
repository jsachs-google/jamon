package org.jamon.eclipse.editor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.JamonProjectPlugin;

public class PreferencesInitializer extends AbstractPreferenceInitializer
{
    private static final String BLACK = SyntaxPreferences.rgbToString(new RGB(0,0,0));
    private static final String DEFAULT = "";
    
    @Override public void initializeDefaultPreferences()
    {
        IPreferenceStore preferenceStore = JamonProjectPlugin.getDefault().getPreferenceStore();
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

}
