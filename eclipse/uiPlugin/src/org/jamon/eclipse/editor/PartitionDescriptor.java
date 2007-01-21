package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.PreferencesStyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public enum PartitionDescriptor {
    ARGS(true, SimpleScanner.makeFactory(SyntaxType.ARGS), "<%args>", "</%args>"),
    XARGS(true, SimpleScanner.makeFactory(SyntaxType.XARGS), "<%xargs>", "</%xargs>"),
    JAVA(true, SimpleScanner.makeFactory(SyntaxType.JAVA), "<%java>", "</%java>"),
    CLASS(true, SimpleScanner.makeFactory(SyntaxType.CLASS), "<%class>", "</%class>"),
    ALIAS(false, SimpleScanner.makeFactory(SyntaxType.ALIAS), "<%alias>", "</%alias>"),
    IMPORT(false, SimpleScanner.makeFactory(SyntaxType.IMPORT),"<%import>", "</%import>"),
    CALL_CONTENT(true, CallScanner.FACTORY, "<&|", "&>"),
    CALL(true, CallScanner.FACTORY, "<&", "&>"),
    WHILE(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%while ", "%>"),
    WHILE_CLOSE(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%while>", ""),
    FOR(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%for ", "%>"),
    FOR_CLOSE(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%for>", ""),
    IF(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%if ", "%>"),
    IF_CLOSE(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%if>", ""),
    ELSE(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%else>", ""),
    ELSEIF(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%elseif ", "%>"),
    DEF(false, SimpleScanner.makeFactory(SyntaxType.DEF), "<%def ", ">"),
    DEF_CLOSE(false, SimpleScanner.makeFactory(SyntaxType.DEF), "</%def>", ""),
    METHOD(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "<%method ", ">"),
    METHOD_CLOSE(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "</%method>", ""),
    EMIT(true, SimpleScanner.makeFactory(SyntaxType.EMIT), "<% ", "%>"),
    DOC(false, DocScanner.makeBoundedScannerFactory(), "<%doc>", "</%doc>");

  private PartitionDescriptor(
      boolean p_hasStrings,
      BoundedScannerFactory p_boundedScannerFactory,
      String p_openTag,
      String p_closeTag)
  {
      m_openTag = p_openTag;
      m_closeTag = p_closeTag;
      m_openChars = p_openTag.toCharArray();
      m_closeChars = p_closeTag.toCharArray();
      m_hasStrings = p_hasStrings;
      m_scannerBoundedScannerFactory = p_boundedScannerFactory;
      m_name = "__jamon_partition_" + name();
      m_token = new Token(m_name);
  }

  public BoundedScanner scanner()
  {
    return m_scannerBoundedScannerFactory.create(
        PreferencesStyleProvider.getProvider(), m_openTag, m_closeTag);
  }

  public IToken token()
  {
    return m_token;
  }

  public String tokenname()
  {
    return m_name;
  }

  public char[] close()
  {
    return m_closeChars;
  }

  public char[] open()
  {
    return m_openChars;
  }

  public boolean hasStrings()
  {
    return m_hasStrings;
  }

  private final String m_openTag, m_closeTag;
  private final char[] m_openChars, m_closeChars;
  private final IToken m_token;
  private final boolean m_hasStrings;
  private final String m_name;
  private final BoundedScannerFactory m_scannerBoundedScannerFactory;
}