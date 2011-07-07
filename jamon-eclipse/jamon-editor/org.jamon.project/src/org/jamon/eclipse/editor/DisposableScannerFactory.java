package org.jamon.eclipse.editor;

import org.jamon.eclipse.editor.preferences.StyleProvider;

public interface DisposableScannerFactory
{
    DisposableScanner create(StyleProvider p_styleProvider, String p_open, String p_close);
}