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

    public static final RGB DEFAULT = new RGB(0, 0, 0);

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
        if (rgb == null)
        {
            return null;
        }
        Color color= m_colorTable.get(rgb);
        if (color == null) 
        {
            color = s_colorFactory.colorFor(rgb);
            m_colorTable.put(rgb, color);
        }
        return color;
    }
    
    public static synchronized JamonColorProvider instance()
    {
      if (s_instance == null)
      {
        s_instance = new JamonColorProvider();
      }
      return s_instance;
    }

    private static ColorFactory s_colorFactory = new DefaultColorFactory();
    
    private JamonColorProvider()
    {
    }
    
    interface ColorFactory
    {
      Color colorFor(RGB p_rgb);
    }
    
    static class DefaultColorFactory implements ColorFactory
    {
      @Override
      public Color colorFor(RGB p_rgb)
      {
        return new Color(Display.getCurrent(), p_rgb);      
      }
    }

    static void setColorFactory(ColorFactory p_colorFactory)
    {
      s_colorFactory = p_colorFactory;
    }
    
    private static JamonColorProvider s_instance;
}
