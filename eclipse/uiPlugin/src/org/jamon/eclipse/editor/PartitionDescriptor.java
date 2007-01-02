/**
 *
 */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

public enum PartitionDescriptor {
  ARGS(true, new SimpleScanner("<%args>", "</%args>", "args")),
  XARGS(true, new SimpleScanner("<%xargs>", "</%xargs>", "args")),
  JAVA(true, new SimpleScanner("<%java>", "</%java>", "java")),
  CLASS(true, new SimpleScanner("<%class>", "</%class>", "class")),
  ALIAS("<%alias>", "</%alias>", false, new SimpleBgScanner("alias")),
  IMPORT(false, new SimpleScanner("<%import>", "</%import>", "import")),
  CALL_CONTENT(true, new CallScanner(true)),
  CALL(true, new CallScanner(false)),
  WHILE(true, new SimpleScanner("<%while ", "%>", "while")),
  WHILE_CLOSE(false, new SimpleScanner("</%while>", "", "while_close")),
  FOR(true, new SimpleScanner("<%for ", "%>", "for")),
  FOR_CLOSE(false, new SimpleScanner("</%for>", "", "for_close")),
  IF(true, new SimpleScanner("<%if ", "%>", "if")),
  IF_CLOSE(false, new SimpleScanner("</%if>", "", "if_close")),
  ELSE(false, new SimpleScanner("<%else>", "", "else")),
  ELSEIF(true, new SimpleScanner("<%elseif ", "%>", "elseif")),
  DEF(false, new SimpleScanner("<%def ", ">", "def")),
  DEF_CLOSE(false, new SimpleScanner("</%def>", "", "def_close")),
  METHOD(false, new SimpleScanner("<%method ", ">", "method")),
  METHOD_CLOSE(false, new SimpleScanner("</%method>", "", "method_close")),
  EMIT(true, new SimpleScanner("<% ", "%>", "emit")),
  DOC(false, new DocScanner());

  private PartitionDescriptor(boolean p_hasStrings, BoundedScanner p_scanner)
  {
    this(p_scanner.open(), p_scanner.close(), p_hasStrings, p_scanner);
  }

  private PartitionDescriptor(char[] p_open, char[] p_close, boolean p_hasStrings, ITokenScanner p_scanner)
  {
    m_open = p_open;
    m_close = p_close;
    m_name = "__jamon_partition_" + name();
    m_token = new Token(m_name);
    m_hasStrings = p_hasStrings;
    m_scanner = p_scanner;
  }

  private PartitionDescriptor(String p_open, String p_close, boolean p_hasStrings, ITokenScanner p_scanner)
  {
    this(p_open.toCharArray(), p_close.toCharArray(), p_hasStrings, p_scanner);
  }

  public ITokenScanner scanner()
  {
    return m_scanner;
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

  public static void reloadScanners() {
      for (PartitionDescriptor partitionDescriptor: values()) {
          if (partitionDescriptor.m_scanner instanceof SimpleScanner) {
            SimpleScanner simpleScanner = (SimpleScanner)partitionDescriptor.m_scanner;
            simpleScanner.reloadColors();
          }
      }
  }

  static {
      PlatformUI.getWorkbench().getThemeManager().addPropertyChangeListener(
          new IPropertyChangeListener() {
              public void propertyChange(PropertyChangeEvent event)
              {
                  if (event.getProperty().startsWith(SimpleScanner.THEME_BASE)) {
                      PartitionDescriptor.reloadScanners();
                  }
              }
          }
      );
  }

  private final char[] m_open;

  private final char[] m_close;

  private final IToken m_token;

  private final boolean m_hasStrings;

  private final String m_name;

  private final ITokenScanner m_scanner;
}