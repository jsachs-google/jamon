package org.jamon.eclipse.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

public class SimpleScanner extends AbstractJavaContainingScanner
{
    public static BoundedScannerFactory makeBoundedScannerFactory(
        final String p_openTag, final String p_closeTag, final String propertyBase)
    {
        return new BoundedScannerFactory() {
            public BoundedScanner create()
            {
                return new SimpleScanner(p_openTag, p_closeTag, propertyBase);
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
        if (themeRgb != null) {
            return themeRgb;
        }
        String s = COLORS.getProperty(propertyName);
        if (s == null)
        {
            return defaultValue;
        }
        else
        {
            String[] rgb = s.trim().split(",");
            return new RGB(Integer.valueOf(rgb[0].trim()), Integer.valueOf(rgb[1].trim()), Integer.valueOf(rgb[2].trim()));
        }
    }

    private static RGB themeColor(String propertyName) {
        return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().getRGB(
            THEME_BASE + propertyName);
    }

    private static final Properties COLORS = new Properties();
    static
    {
        InputStream in = SimpleScanner.class.getResourceAsStream("colors.properties");
        if (in != null)
        {
            try
            {
                COLORS.load(in);
            }
            catch (IOException e)
            {
                System.err.println("Unable to load colors");
            }
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                // sshh
            }
        }
    }

    protected SimpleScanner(String p_openTag, String p_closeTag, String propertyBase)
    {
        super(p_openTag, p_closeTag, fg(propertyBase), bg(propertyBase));
    }
}
