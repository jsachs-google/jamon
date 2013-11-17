/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IWordDetector;

public class JamonWordDetector implements IWordDetector {

    @Override
    public boolean isWordPart(char character) {
        return Character.isJavaIdentifierPart(character);
    }
    
    @Override
    public boolean isWordStart(char character) {
        return Character.isJavaIdentifierStart(character);
    }
}
