package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class JamonTagMatchingRule implements IRule {
    
    public JamonTagMatchingRule(String tagName, IToken token) {
        this.tagName = tagName;
        this.token = token;
    }

    public IToken evaluate(ICharacterScanner scanner) {
        int c = scanner.read();
        if (c == ICharacterScanner.EOF) {
            return Token.EOF;
        }
        int read = 1;
        if (c == '<') {
            c = scanner.read();
            read++;
            if (c == '/') {
                c = scanner.read();
                read++;
            }
            if (c == '%') {
	            c = scanner.read();
	            read++;
	            int i = 0;
	            while (i < tagName.length() && c == tagName.charAt(i)) {
	                c = scanner.read();
	                read++;
	                i++;
	            }
	            if (i == tagName.length()) {
	            	if (c == '>')
	            	{
	            		return token;
	            	}
	            }
            }
        }
        while (read-- > 0) {
        	scanner.unread();
        }
        return Token.UNDEFINED;
    }
    
    private final String tagName;
    private final IToken token;
}
