/**
 * 
 */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

public enum PartitionDescriptor {
  ARGS("<%args>", "</%args>", true, new ArgsScanner()), 
  XARGS("<%xargs>", "</%xargs>", true, new XargsScanner()), 
  JAVA("<%java>", "</%java>", true, new JavaScanner()),
  CLASS("<%class>", "</%class>", true, new ClassScanner()),
  ALIAS("<%alias>", "</%alias>", false, new RGB(255, 236, 236)),
  IMPORT("<%import>", "</%import>", false, new ImportScanner()),
  CALL_CONTENT("<&|", "&>", true, new CallScanner(true)),
  CALL("<&", "&>", true, new CallScanner(false)),
  WHILE("<%while ", "%>", true, new ControlScanner("<%while ", "%>", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  WHILE_CLOSE("</%while>", "", false, new ControlScanner("</%while>", "", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  FOR("<%for ", "%>", true, new ControlScanner("<%for ", "%>", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  FOR_CLOSE("</%for>", "", false, new ControlScanner("</%for>", "", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  IF("<%if ", "%>", true, new ControlScanner("<%if ", "%>", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  IF_CLOSE("</%if>", "", false, new ControlScanner("</%if>", "", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  ELSE("<%else>", "", false, new ControlScanner("<%else>", "", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  ELSEIF("<%elseif ", "%>", false, new ControlScanner("<%elseif ", "%>", new RGB(0, 127, 255), new RGB(224, 248, 255))),
  EMIT("<% ", "%>", true, new EmitScanner()),
  DOC("<%doc>", "</%doc>", false, new DocScanner());
  
  private PartitionDescriptor(String p_open, String p_close, boolean p_hasStrings, RGB p_bgcolor)
  { 
    this(p_open, p_close, p_hasStrings, new SimpleBgScanner(p_bgcolor));
  }
  
  private PartitionDescriptor(String p_open, String p_close, boolean p_hasStrings, ITokenScanner p_scanner)
  {
    m_open = p_open.toCharArray();
    m_close = p_close.toCharArray();
    m_name = "__jamon_partition_" + name();
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