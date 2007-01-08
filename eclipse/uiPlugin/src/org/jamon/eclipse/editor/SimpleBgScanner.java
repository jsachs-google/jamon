package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class SimpleBgScanner extends BufferedRuleBasedScanner implements BoundedScanner
{
    public static BoundedScannerFactory makeFactory(
        final String p_open, final String p_close, final String p_propertyBase)
    {
        return new BoundedScannerFactory() {
            public BoundedScanner create()
            {
                return new SimpleBgScanner(p_open, p_close, p_propertyBase);
            }
        };
    }

    public SimpleBgScanner(String p_open, String p_close, String p_propertyBase)
    {
        final JamonColorProvider provider = JamonColorProvider.instance();
        final IToken defaultToken = new Token(
            new TextAttribute(
                provider.getColor(SimpleScanner.fg(p_propertyBase)),
                provider.getColor(SimpleScanner.bg(p_propertyBase)),
                SWT.NORMAL));
        setDefaultReturnToken(defaultToken);
        m_open = p_open.toCharArray();
        m_close = p_close.toCharArray();
    }

    public char[] open()
    {
        return m_open;
    }

    public char[] close()
    {
        return m_close;
    }

    private final char[] m_open, m_close;
}
