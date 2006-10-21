package org.jamon.eclipse.editor;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Manager for colors used in the Java editor
 */
public class JamonColorProvider
{

    public static final RGB MULTI_LINE_COMMENT = new RGB(128, 0, 0);
    public static final RGB SINGLE_LINE_COMMENT = new RGB(128, 128, 0);
    public static final RGB KEYWORD = new RGB(0, 0, 255);
    public static final RGB JAMON_KEYWORD = new RGB(0, 128, 255);
    public static final RGB TYPE = new RGB(0, 128, 128);
    public static final RGB STRING = new RGB(0, 192, 0);
    public static final RGB DEFAULT = new RGB(0, 0, 0);
    public static final RGB JAVADOC_KEYWORD = new RGB(0, 128, 0);
    public static final RGB JAVADOC_TAG = new RGB(128, 128, 128);
    public static final RGB JAVADOC_LINK = new RGB(128, 128, 128);
    public static final RGB JAVADOC_DEFAULT = new RGB(0, 128, 128);
    public static final RGB JAMON_CODE_BG = new RGB(224,224,255);

    public static final RGB JAMON_BG = new RGB(255, 255, 255);
    public static final RGB ARGS_BG = new RGB(255, 255, 224);
    public static final RGB JAVA_BG = new RGB(224, 255, 255);
    public static final RGB EMIT_BG = new RGB(255, 224, 255);
    public static final RGB CLASS_BG = new RGB(224, 224, 255);

    private final Map<RGB,Color> m_colorTable= new HashMap<RGB,Color>(10);

    /**
     * Release all of the color resources held onto by the receiver.
     */ 
    public void dispose()
    {
    	for (Color c : m_colorTable.values())
    	{
    		c.dispose();
    	}
    }
    
    /**
     * Return the color that is stored in the color table under the given RGB
     * value.
     * 
     * @param rgb the RGB value
     * @return the color stored in the color table for the given RGB value
     */
    public Color getColor(RGB rgb)
    {
        Color color= m_colorTable.get(rgb);
        if (color == null) 
        {
            color= new Color(Display.getCurrent(), rgb);
            m_colorTable.put(rgb, color);
        }
        return color;
    }
}
