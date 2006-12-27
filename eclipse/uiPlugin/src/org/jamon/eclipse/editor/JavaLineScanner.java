package org.jamon.eclipse.editor;

import org.eclipse.swt.graphics.RGB;

public class JavaLineScanner extends AbstractJavaContainingScanner
{
    protected JavaLineScanner()
    {
        super("\n%", "\n", new RGB(0, 64, 16), new RGB(240, 255, 240));
    }
}
