/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * A Java code scanner.
 */
public class JamonJavaCodeScanner implements ITokenScanner {

    private static String[] fgKeywords= { "abstract", "break", "case", "catch", "class", "continue", "default", "do", "else", "extends", "final", "finally", "for", "if", "implements", "import", "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "volatile", "while" };

    private static String[] fgTypes= { "void", "boolean", "char", "byte", "short", "int", "long", "float", "double" };

    private static String[] fgConstants= { "false", "null", "true" };

    /**
     * Creates a Java code scanner with the given color provider.
     * 
     * @param provider the color provider
     */
    public JamonJavaCodeScanner(JamonColorProvider provider, RGB bgcolor) {

        final Color bg = provider.getColor(bgcolor);
        IToken keyword= new Token(new TextAttribute(provider.getColor(KEYWORD), bg, SWT.NORMAL));
        IToken type= new Token(new TextAttribute(provider.getColor(TYPE), bg, SWT.NORMAL));
        IToken string= new Token(new TextAttribute(provider.getColor(STRING), bg, SWT.NORMAL));
        IToken comment= new Token(new TextAttribute(provider.getColor(SINGLE_LINE_COMMENT), bg, SWT.NORMAL));
        IToken other= new Token(new TextAttribute(provider.getColor(DEFAULT), bg, SWT.NORMAL));
        IToken whitespace = new Token(new TextAttribute(provider.getColor(DEFAULT), bg, SWT.NORMAL));
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
        rules.add(new JamonWhitespaceRule(whitespace));
        scanner = new RuleBasedScanner();
        scanner.setDefaultReturnToken(other);
        IRule[] result= new IRule[rules.size()];
        rules.toArray(result);
        scanner.setRules(result);
    }
    
    @Override
    public int getTokenLength()
    {
      return scanner.getTokenLength();
    }
    
    @Override
    public int getTokenOffset()
    {
      return scanner.getTokenOffset();
    }
    
    @Override
    public IToken nextToken()
    {
      return scanner.nextToken();
    }
    
    @Override
    public void setRange(IDocument p_document, int p_offset, int p_length)
    {
      scanner.setRange(p_document, p_offset, p_length);
    }
    
    private final RuleBasedScanner scanner;

    private static final RGB SINGLE_LINE_COMMENT = new RGB(128, 128, 0);
    private static final RGB KEYWORD = new RGB(0, 0, 255);
    private static final RGB TYPE = new RGB(0, 128, 128);
    private static final RGB STRING = new RGB(0, 192, 0);
    private static final RGB DEFAULT = JamonColorProvider.DEFAULT;
}
