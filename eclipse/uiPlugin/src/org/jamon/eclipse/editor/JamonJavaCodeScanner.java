package org.jamon.eclipse.editor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

/**
 * A Java code scanner.
 */
public class JamonJavaCodeScanner extends RuleBasedScanner {

    private static String[] fgKeywords= { "abstract", "break", "case", "catch", "class", "continue", "default", "do", "else", "extends", "final", "finally", "for", "if", "implements", "import", "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "volatile", "while" };

    private static String[] fgTypes= { "void", "boolean", "char", "byte", "short", "int", "long", "float", "double" };

    private static String[] fgConstants= { "false", "null", "true" };

    /**
     * Creates a Java code scanner with the given color provider.
     * 
     * @param provider the color provider
     */
    public JamonJavaCodeScanner(JamonColorProvider provider) {

        IToken keyword= new Token(new TextAttribute(provider.getColor(JamonColorProvider.KEYWORD)));
        IToken type= new Token(new TextAttribute(provider.getColor(JamonColorProvider.TYPE)));
        IToken string= new Token(new TextAttribute(provider.getColor(JamonColorProvider.STRING)));
        IToken comment= new Token(new TextAttribute(provider.getColor(JamonColorProvider.SINGLE_LINE_COMMENT)));
        IToken other= new Token(new TextAttribute(provider.getColor(JamonColorProvider.DEFAULT)));

        List<IRule> rules= new ArrayList<IRule>();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", comment));

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\'));

        // Add word rule for keywords, types, and constants.
        WordRule wordRule= new WordRule(new JamonWordDetector(), other);
        for (int i= 0; i < fgKeywords.length; i++)
            wordRule.addWord(fgKeywords[i], keyword);
        for (int i= 0; i < fgTypes.length; i++)
            wordRule.addWord(fgTypes[i], type);
        for (int i= 0; i < fgConstants.length; i++)
            wordRule.addWord(fgConstants[i], type);
        rules.add(wordRule);
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new JamonWhitespaceDetector()));
        setDefaultReturnToken(new Token(other));
        IRule[] result= new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
