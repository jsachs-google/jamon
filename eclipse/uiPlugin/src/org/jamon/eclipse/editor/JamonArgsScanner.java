package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class JamonArgsScanner extends BufferedRuleBasedScanner {
    public JamonArgsScanner(JamonColorProvider provider) {
        super(10);
        
        final IToken keyword= new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)));
        final IToken type= new Token(new TextAttribute(provider.getColor(JamonColorProvider.TYPE)));

        List<IRule> rules= new ArrayList<IRule>();
        rules.add(new JamonTagMatchingRule("args", keyword));
        rules.add(new IRule() {
            public IToken evaluate(ICharacterScanner scanner) {
                return type;
            }
        });
        setRules(rules.toArray(new IRule[rules.size()]));
    }
}
