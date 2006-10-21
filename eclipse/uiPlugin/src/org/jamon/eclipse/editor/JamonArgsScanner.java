package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JamonArgsScanner extends BufferedRuleBasedScanner 
{
    public JamonArgsScanner(JamonColorProvider provider) 
    {
        super();
        final IToken defaultToken= new Token(new TextAttribute(provider.getColor(JamonColorProvider.TYPE)));
        setDefaultReturnToken(defaultToken);
    }

}
