/**
 * 
 */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public enum PartitionDescriptor {
  ARGS("<%args>", "</%args>", true, new RGB(255, 255, 224)), 
  XARGS("<%xargs>", "</%xargs>", true, new RGB(224, 255, 255)), 
  JAVA("<%java>", "</%java>", true, new RGB(255, 224, 255)),
  CLASS("<%class>", "</%class>", true, new RGB(224, 255, 224)),
  ALIAS("<%alias>", "</%alias>", false, new RGB(255, 224, 224)),
  IMPORT("<%import>", "</%import>", false, new RGB(192, 224, 255)),
  CALL_CONTENT("<&|", "&>", true, new RGB(192, 255, 224)),
  CALL("<&", "&>", true, new CallScanner()),
  FOR("<%for ", "%>", true, new RGB(224, 255, 192)),
  FOR_CLOSE("</%for>", "", false, new RGB(224, 255, 192)),
  EMIT("<% ", "%>", true, new RGB(224, 224, 255)),
  DOC("<%doc>", "</%doc>", false, new RGB(160, 160, 255));
  
  private PartitionDescriptor(String p_open, String p_close, boolean p_hasStrings, RGB p_bgcolor)
  { 
    this(p_open, p_close, p_hasStrings, new SimpleBgScanner(p_bgcolor));
  }
  
  private PartitionDescriptor(String p_open, String p_close, boolean p_hasStrings, ITokenScanner p_scanner)
  {
    m_open = p_open.toCharArray();
    m_close = p_close.toCharArray();
    m_name = "__jamon_parition_" + name();
    m_token = new Token(m_name);
    m_hasStrings = p_hasStrings;
    m_scanner = p_scanner;
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

  private final char[] m_open;

  private final char[] m_close;

  private final IToken m_token;

  private final boolean m_hasStrings;

  private final String m_name;

  private final ITokenScanner m_scanner;
}