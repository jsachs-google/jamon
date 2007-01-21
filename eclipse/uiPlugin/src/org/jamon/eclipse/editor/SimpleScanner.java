package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public class SimpleScanner extends AbstractJavaContainingScanner
{
    public final static BoundedScannerFactory makeFactory(final SyntaxType p_syntaxType)
    {
        return new BoundedScannerFactory() {
            public BoundedScanner create(
                StyleProvider p_styleProvider,
                String p_openTag,
                String p_closeTag)
            {
                return new SimpleScanner(p_openTag, p_closeTag, p_syntaxType, p_styleProvider);
            }
        };
    }

    public static final String THEME_BASE = "org.jamon.theme.";

    static RGB fg(String propertyBase)
    {
        return color(propertyBase + ".foreground", JamonColorProvider.DEFAULT);
    }

    static RGB bg(String propertyBase)
    {
        return color(propertyBase + ".background", JamonColorProvider.DEFAULT);
    }

    private static RGB color(String propertyName, RGB defaultValue)
    {
        RGB themeRgb = themeColor(propertyName);
        return themeRgb != null ? themeRgb : defaultValue;
    }

    private static RGB themeColor(String propertyName) {
        return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().getRGB(
            THEME_BASE + propertyName);
    }

    @Deprecated protected SimpleScanner(String p_openTag, String p_closeTag, String propertyBase)
    {
        super(p_openTag, p_closeTag, fg(propertyBase), bg(propertyBase));
    }

    protected SimpleScanner(
        String p_openTag, String p_closeTag, SyntaxType p_syntaxType, StyleProvider p_styleProvider)
    {
        super(p_openTag, p_closeTag, p_syntaxType, p_styleProvider);
    }
}
