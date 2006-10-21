package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class JamonJavaScanner extends BufferedRuleBasedScanner 
{
    public JamonJavaScanner(JamonColorProvider provider) 
    {
        super();
        final IToken defaultToken= new Token(new TextAttribute(provider.getColor(JamonColorProvider.DEFAULT), provider.getColor(JamonColorProvider.JAVA_BG), SWT.NORMAL));
        setDefaultReturnToken(defaultToken);
    }

}
