package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JamonScanner extends BufferedRuleBasedScanner {
	public JamonScanner(JamonColorProvider provider) {
        final IToken defaultToken= new Token(new TextAttribute(provider.getColor(JamonColorProvider.DEFAULT)));
        final List<IRule> rules= new ArrayList<IRule>();
        rules.add(new JamonTagMatchingRule("args", new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)))));
        rules.add(new JamonTagMatchingRule("alias", new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)))));
        rules.add(new JamonTagMatchingRule("import", new Token(new TextAttribute(provider.getColor(JamonColorProvider.JAMON_KEYWORD)))));
        setDefaultReturnToken(defaultToken);
        // TODO: are these useful here?
        /*
        IToken string= new Token(new TextAttribute(provider.getColor(JamonColorProvider.STRING)));
        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\'));

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new JamonWhitespaceDetector()));
        */
        setRules(rules.toArray(new IRule[rules.size()]));
	}
}
