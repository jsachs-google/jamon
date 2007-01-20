package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public class SimpleScanner extends AbstractJavaContainingScanner
{
    public static BoundedScannerFactory makeBoundedScannerFactory(
        final String p_openTag, final String p_closeTag)
    {
        return new BoundedScannerFactory() {
            public BoundedScanner create(SyntaxType p_syntaxType)
            {
                return new SimpleScanner(p_openTag, p_closeTag, p_syntaxType);
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

    protected SimpleScanner(String p_openTag, String p_closeTag, String propertyBase)
    {
        super(p_openTag, p_closeTag, fg(propertyBase), bg(propertyBase));
    }

    protected SimpleScanner(String p_openTag, String p_closeTag, SyntaxType p_syntaxType)
    {
        super(p_openTag, p_closeTag, p_syntaxType);
    }
}
