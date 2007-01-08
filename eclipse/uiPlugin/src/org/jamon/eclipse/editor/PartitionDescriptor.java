package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public enum PartitionDescriptor {
  ARGS(true, SimpleScanner.makeBoundedScannerFactory("<%args>", "</%args>", "args")),
  XARGS(true, SimpleScanner.makeBoundedScannerFactory("<%xargs>", "</%xargs>", "args")),
  JAVA(true, SimpleScanner.makeBoundedScannerFactory("<%java>", "</%java>", "java")),
  CLASS(true, SimpleScanner.makeBoundedScannerFactory("<%class>", "</%class>", "class")),
  ALIAS(false, SimpleBgScanner.makeFactory("<%alias>", "</%alias>", "alias")),
  IMPORT(false, SimpleScanner.makeBoundedScannerFactory("<%import>", "</%import>", "import")),
  CALL_CONTENT(true, CallScanner.makeBoundedScannerFactory(true)),
  CALL(true, CallScanner.makeBoundedScannerFactory(false)),
  WHILE(true, SimpleScanner.makeBoundedScannerFactory("<%while ", "%>", "while")),
  WHILE_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%while>", "", "while_close")),
  FOR(true, SimpleScanner.makeBoundedScannerFactory("<%for ", "%>", "for")),
  FOR_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%for>", "", "for_close")),
  IF(true, SimpleScanner.makeBoundedScannerFactory("<%if ", "%>", "if")),
  IF_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%if>", "", "if_close")),
  ELSE(false, SimpleScanner.makeBoundedScannerFactory("<%else>", "", "else")),
  ELSEIF(true, SimpleScanner.makeBoundedScannerFactory("<%elseif ", "%>", "elseif")),
  DEF(false, SimpleScanner.makeBoundedScannerFactory("<%def ", ">", "def")),
  DEF_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%def>", "", "def_close")),
  METHOD(false, SimpleScanner.makeBoundedScannerFactory("<%method ", ">", "method")),
  METHOD_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%method>", "", "method_close")),
  EMIT(true, SimpleScanner.makeBoundedScannerFactory("<% ", "%>", "emit")),
  DOC(false, DocScanner.makeBoundedScannerFactory());

  private PartitionDescriptor(boolean p_hasStrings, BoundedScannerFactory p_scannerBoundedScannerFactory)
  {
    this(
        new OpenClosePair(p_scannerBoundedScannerFactory), p_hasStrings, p_scannerBoundedScannerFactory);
  }

  private static class OpenClosePair
  {
      private final char[] open, close;
      public OpenClosePair(BoundedScannerFactory p_scannerBoundedScannerFactory)
      {
          BoundedScanner scanner = p_scannerBoundedScannerFactory.create();
          open = scanner.open();
          close = scanner.close();
      }
  }

  private PartitionDescriptor(
      OpenClosePair p_openClosePair, boolean p_hasStrings, BoundedScannerFactory p_scannerBoundedScannerFactory)
  {
      this(p_openClosePair.open, p_openClosePair.close, p_hasStrings, p_scannerBoundedScannerFactory);
  }

  private PartitionDescriptor(
      char[] p_open, char[] p_close, boolean p_hasStrings, BoundedScannerFactory p_scannerBoundedScannerFactory)
  {
    m_open = p_open;
    m_close = p_close;
    m_name = "__jamon_partition_" + name();
    m_token = new Token(m_name);
    m_hasStrings = p_hasStrings;
    m_scannerBoundedScannerFactory = p_scannerBoundedScannerFactory;
  }

  public ITokenScanner scanner()
  {
    return m_scannerBoundedScannerFactory.create();
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
    return m_close;
  }

  public char[] open()
  {
    return m_open;
  }

  public boolean hasStrings()
  {
    return m_hasStrings;
  }

  private final char[] m_open;

  private final char[] m_close;

  private final IToken m_token;

  private final boolean m_hasStrings;

  private final String m_name;

  private final BoundedScannerFactory m_scannerBoundedScannerFactory;
}