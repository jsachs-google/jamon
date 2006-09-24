package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class JamonImportScanner extends BufferedRuleBasedScanner {
    public JamonImportScanner(JamonColorProvider provider) {
        super(10);
        
        final IToken keyword= new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)));
        final IToken other= new Token(new TextAttribute(provider.getColor(JamonColorProvider.DEFAULT)));

        List<IRule> rules= new ArrayList<IRule>();
        rules.add(new JamonTagMatchingRule("import", keyword));
        rules.add(new IRule() {
            public IToken evaluate(ICharacterScanner scanner) {
                return other;
            }
        });
        setRules(rules.toArray(new IRule[rules.size()]));
    }
}
