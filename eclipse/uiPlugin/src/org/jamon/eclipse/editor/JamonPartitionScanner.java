package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class JamonPartitionScanner extends RuleBasedPartitionScanner {

        public final static String JAVA_MULTILINE_COMMENT= "__java_multiline_comment"; //$NON-NLS-1$
        public final static String JAMON_DOC = "__jamon_doc"; //$NON-NLS-1$
        public final static String JAMON_JAVA = "__jamon_java"; //$NON-NLS-1$
        public final static String JAMON_ARGS = "__jamon_args"; //$NON-NLS-1$
        public final static String JAMON_IMPORT = "__jamon_import"; //$NON-NLS-1$
        public final static String[] JAVA_PARTITION_TYPES= new String[] { JAMON_DOC, JAMON_JAVA, JAMON_ARGS, JAMON_IMPORT };

        public JamonPartitionScanner() {
            super();

            IToken jamonDoc = new Token(JAMON_DOC);
            IToken jamonJava = new Token(JAMON_JAVA);
            IToken jamonArgs = new Token(JAMON_ARGS);
            IToken jamonImport = new Token(JAMON_IMPORT);

            List<IRule> rules= new ArrayList<IRule>();

            rules.add(new MultiLineRule("<%doc>", "</%doc>", jamonDoc, (char) 0, true));
            rules.add(new MultiLineRule("<%args>", "</%args>", jamonArgs, (char) 0, true));
            rules.add(new MultiLineRule("<%import>", "</%import>", jamonImport, (char) 0, true));
            rules.add(new MultiLineRule("<%java>", "</%java>", jamonJava, (char) 0, true));

            setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
        }
    }
