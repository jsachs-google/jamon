/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import org.jamon.eclipse.editor.preferences.StyleProvider;

public interface DisposableScannerFactory
{
    DisposableScanner create(StyleProvider p_styleProvider, String p_open, String p_close);
}