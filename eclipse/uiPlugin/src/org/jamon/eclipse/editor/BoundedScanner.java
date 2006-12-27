package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.ITokenScanner;

public interface BoundedScanner extends ITokenScanner
{
    char[] open();
    char[] close();
}
