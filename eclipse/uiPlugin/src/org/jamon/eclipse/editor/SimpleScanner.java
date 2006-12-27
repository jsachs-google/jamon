package org.jamon.eclipse.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.graphics.RGB;

public class SimpleScanner extends AbstractJavaContainingScanner
{
    static RGB fg(String propertyBase)
    {
        return color(propertyBase + ".foreground");
    }
    
    static RGB bg(String propertyBase)
    {
        return color(propertyBase + ".background");
    }
    
    private static RGB color(String propertyName)
    {
        String s = COLORS.getProperty(propertyName);
        if (s == null)
        {
            return null;
        }
        else
        {
            String[] rgb = s.trim().split(",");
            return new RGB(Integer.valueOf(rgb[0].trim()), Integer.valueOf(rgb[1].trim()), Integer.valueOf(rgb[2].trim()));
        }
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
