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

        public final static String JAMON_DOC = "__jamon_doc";
        public final static String JAMON_JAVA = "__jamon_java";
        public final static String JAMON_CLASS = "__jamon_class";
        public final static String JAMON_ARGS = "__jamon_args";
        public final static String JAMON_XARGS = "__jamon_xargs";
        public final static String JAMON_IMPORT = "__jamon_import";
        public final static String JAMON_ALIAS = "__jamon_alias";
        public final static String[] JAMON_PARTITION_TYPES= new String[] { 
        	JAMON_DOC, 
        	JAMON_JAVA, 
        	JAMON_ARGS, 
        	JAMON_XARGS, 
        	JAMON_IMPORT, 
        	JAMON_ALIAS,
        	JAMON_CLASS,
        	};

        public JamonPartitionScanner() {
            super();

            IToken jamonDoc = new Token(JAMON_DOC);
            IToken jamonJava = new Token(JAMON_JAVA);
            IToken jamonClass = new Token(JAMON_CLASS);
            IToken jamonArgs = new Token(JAMON_ARGS);
            IToken jamonXargs = new Token(JAMON_XARGS);
            IToken jamonImport = new Token(JAMON_IMPORT);
            IToken jamonAlias = new Token(JAMON_ALIAS);

            List<IRule> rules= new ArrayList<IRule>();

            rules.add(new MultiLineRule("<%doc>", "</%doc>", jamonDoc, (char) 0, true));
            rules.add(new MultiLineRule("<%java>", "</%java>", jamonJava, (char) 0, true));
            rules.add(new MultiLineRule("<%args>", "</%args>", jamonArgs, (char) 0, true));
            rules.add(new MultiLineRule("<%xargs>", "</%xargs>", jamonXargs, (char) 0, true));
            rules.add(new MultiLineRule("<%import>", "</%import>", jamonImport, (char) 0, true));
            rules.add(new MultiLineRule("<%alias>", "</%alias>", jamonAlias, (char) 0, true));
            rules.add(new MultiLineRule("<%class>", "</%class>", jamonClass, (char) 0, true));
            setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
        }
    }
