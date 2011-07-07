package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.ITokenScanner;

public interface DisposableScanner extends ITokenScanner
{
    void dispose();
}
