package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class SimpleBgScanner extends BufferedRuleBasedScanner 
{
    public SimpleBgScanner(JamonColorProvider provider, RGB bgColor) 
    {
        final IToken defaultToken= new Token(new TextAttribute(provider.getColor(JamonColorProvider.DEFAULT), provider.getColor(bgColor), SWT.NORMAL));
        setDefaultReturnToken(defaultToken);
    }

}
