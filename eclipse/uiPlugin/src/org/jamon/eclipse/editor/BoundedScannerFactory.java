package org.jamon.eclipse.editor;

import org.jamon.eclipse.editor.preferences.SyntaxType;

public interface BoundedScannerFactory
{
    BoundedScanner create(SyntaxType p_syntaxType);
}