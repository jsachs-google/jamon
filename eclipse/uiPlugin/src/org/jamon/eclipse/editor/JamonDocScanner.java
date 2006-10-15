package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class JamonDocScanner extends BufferedRuleBasedScanner {
    public JamonDocScanner(JamonColorProvider provider) {
        super(10);
        
        final IToken keyword= new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)));
        final IToken comment= new Token(new TextAttribute(provider.getColor(JamonColorProvider.SINGLE_LINE_COMMENT), provider.getColor(JamonColorProvider.JAMON_CODE_BG), 0));

        List<IRule> rules= new ArrayList<IRule>();
        rules.add(new JamonTagMatchingRule("doc", keyword));
        setDefaultReturnToken(comment);
        /*
        rules.add(new IRule() {
            public IToken evaluate(ICharacterScanner scanner) {
                return comment;
            }
        }
        );
        */
        setRules(rules.toArray(new IRule[rules.size()]));
    }
}
