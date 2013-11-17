/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
        make(true, SimpleScanner.makeFactory(SyntaxType.ARGS), "<%args>", "</%args>", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.XARGS), "<%xargs>", "</%xargs>", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.JAVA), "<%java>", "</%java>", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.CLASS), "<%class>", "</%class>", false);
        make(false, SimpleScanner.makeFactory(SyntaxType.ALIAS), "<%alias>", "</%alias>", false);
        make(false, SimpleScanner.makeFactory(SyntaxType.IMPORT),"<%import>", "</%import>", false);
        make(true, CallScanner.FACTORY, "<&||", "&>", false);
        make(true, CallScanner.FACTORY, "<&|", "&>", false);
        make(true, CallScanner.FACTORY, "<&", "&>", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%while", "%>", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%while>", "", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%for", "%>", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%for>", "", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%if", "%>", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "</%if>", "", false);
        make(false, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%else>", "", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.FLOW_CONTROL), "<%elseif", "%>", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.DEF), "<%def", ">", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.DEF), "</%def>", "", false);
        make(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "<%method", ">", true);
        make(false, SimpleScanner.makeFactory(SyntaxType.METHOD), "</%method>", "", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.EMIT), "<%", "%>", true);
        make(false, DocScanner.makeDisposableScannerFactory(), "<%doc>", "</%doc>", false);
        make(true, SimpleScanner.makeFactory(SyntaxType.JAVA), "<%java", "%>", true);
    }

    public static Iterable<PartitionDescriptor> values() { return VALUES; }

    private static int counter = 0;

    private static void make(
        boolean p_hasStrings,
        DisposableScannerFactory p_disposableScannerFactory,
        String p_openTag,
        String p_closeTag,
        boolean p_openTagRequiresWhitespace)
    {
        VALUES.add(new PartitionDescriptor(
            p_hasStrings, p_disposableScannerFactory, p_openTag, p_closeTag, p_openTagRequiresWhitespace));
    }


  private PartitionDescriptor(
      boolean p_hasStrings,
      DisposableScannerFactory p_disposableScannerFactory,
      String p_openTag,
      String p_closeTag,
      boolean p_openTagWhitespace)
  {
      m_openTag = p_openTag;
      m_closeTag = p_closeTag;
      m_openChars = p_openTag.toCharArray();
      m_closeChars = p_closeTag.toCharArray();
      m_hasStrings = p_hasStrings;
      m_openTagWhitespace = p_openTagWhitespace;
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

  public boolean openTagRequiresTrailingWhitespace()
  {
    return m_openTagWhitespace;
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
  private final boolean m_openTagWhitespace;
  private final DisposableScannerFactory m_scannerDisposableScannerFactory;
}