package org.jamon.eclipse.editor;


import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class JamonWhitespaceDetector implements IWhitespaceDetector {

    /* (non-Javadoc)
     * Method declared on IWhitespaceDetector
     */
    public boolean isWhitespace(char character) {
        return Character.isWhitespace(character);
    }
}
