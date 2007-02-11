package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.PreferencesStyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public class PartitionDescriptor {

    private final static List<PartitionDescriptor> VALUES = new ArrayList<PartitionDescriptor>();
    static
    {
        make(true, SimpleScanner.makeFactory(SyntaxType.ARGS), "<%args>", "</%args>");
        make(true, SimpleScanner.makeFactory(SyntaxType.XARGS), "<%xargs>", "</%xargs>");
        make(true, SimpleScanner.makeFactory(SyntaxType.JAVA), "<%java>", "</%java>");
        make(true, SimpleScanner.makeFactory(SyntaxType.CLASS), "<%class>", "</%class>");
        make(false, SimpleScanner.makeFactory(SyntaxType.ALIAS), "<%alias>", "</%alias>");
        make(false, SimpleScanner.makeFactory(SyntaxType.IMPORT),"<%import>", "</%import>");
        make(true, CallScanner.FACTORY, "<&|", "&>");
        make(true, CallScanner.FACTORY, "<&", "&>");
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%while ", "%>");
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%while>", "");
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%for ", "%>");
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%for>", "");
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%if ", "%>");
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%if>", "");
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%else>", "");
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%elseif ", "%>");
        make(false, SimpleScanner.makeFactory(SyntaxType.DEF), "<%def ", ">");
        make(false, SimpleScanner.makeFactory(SyntaxType.DEF), "</%def>", "");
        make(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "<%method ", ">");
        make(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "</%method>", "");
        make(true, SimpleScanner.makeFactory(SyntaxType.EMIT), "<% ", "%>");
        make(false, DocScanner.makeDisposableScannerFactory(), "<%doc>", "</%doc>");

        char[] whitespaceChars = new char[] { ' ', '\n', '\t', '\r', '\f' };
        for (char whitespaceChar: whitespaceChars)
        {
            make(true, SimpleScanner.makeFactory(SyntaxType.JAVA), "<%java" + whitespaceChar, "%>");
        }
    }

    public static Iterable<PartitionDescriptor> values() { return VALUES; }

    private static int counter = 0;

    private static void make(
        boolean p_hasStrings,
        DisposableScannerFactory p_disposableScannerFactory,
        String p_openTag,
        String p_closeTag)
    {
        VALUES.add(new PartitionDescriptor(
            p_hasStrings, p_disposableScannerFactory, p_openTag, p_closeTag));
    }


    private PartitionDescriptor(
      boolean p_hasStrings,
      DisposableScannerFactory p_disposableScannerFactory,
      String p_openTag,
      String p_closeTag)
  {
      m_openTag = p_openTag;
      m_closeTag = p_closeTag;
      m_openChars = p_openTag.toCharArray();
      m_closeChars = p_closeTag.toCharArray();
      m_hasStrings = p_hasStrings;
      m_scannerDisposableScannerFactory = p_disposableScannerFactory;
      m_name = "__jamon_partition_" + counter++;
      m_token = new Token(m_name);
  }

  public DisposableScanner scanner()
  {
    return m_scannerDisposableScannerFactory.create(
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
  private final DisposableScannerFactory m_scannerDisposableScannerFactory;
}