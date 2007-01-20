package org.jamon.eclipse.editor;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public enum PartitionDescriptor {
  ARGS(true, SimpleScanner.makeBoundedScannerFactory("<%args>", "</%args>"), SyntaxType.ARGS),
  XARGS(true, SimpleScanner.makeBoundedScannerFactory("<%xargs>", "</%xargs>"), SyntaxType.XARGS),
  JAVA(true, SimpleScanner.makeBoundedScannerFactory("<%java>", "</%java>"), SyntaxType.JAVA),
  CLASS(true, SimpleScanner.makeBoundedScannerFactory("<%class>", "</%class>"), SyntaxType.CLASS),
  ALIAS(false, SimpleScanner.makeBoundedScannerFactory("<%alias>", "</%alias>"), SyntaxType.ALIAS),
  IMPORT(false, SimpleScanner.makeBoundedScannerFactory("<%import>", "</%import>"), SyntaxType.IMPORT),
  CALL_CONTENT(true, CallScanner.makeBoundedScannerFactory(true), SyntaxType.CALL_CONTENT),
  CALL(true, CallScanner.makeBoundedScannerFactory(false), SyntaxType.CALL),
  WHILE(true, SimpleScanner.makeBoundedScannerFactory("<%while ", "%>"), SyntaxType.FLOW_CONTROL),
  WHILE_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%while>", ""), SyntaxType.FLOW_CONTROL),
  FOR(true, SimpleScanner.makeBoundedScannerFactory("<%for ", "%>"), SyntaxType.FLOW_CONTROL),
  FOR_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%for>", ""), SyntaxType.FLOW_CONTROL),
  IF(true, SimpleScanner.makeBoundedScannerFactory("<%if ", "%>"), SyntaxType.FLOW_CONTROL),
  IF_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%if>", ""), SyntaxType.FLOW_CONTROL),
  ELSE(false, SimpleScanner.makeBoundedScannerFactory("<%else>", ""), SyntaxType.FLOW_CONTROL),
  ELSEIF(true, SimpleScanner.makeBoundedScannerFactory("<%elseif ", "%>"), SyntaxType.FLOW_CONTROL),
  DEF(false, SimpleScanner.makeBoundedScannerFactory("<%def ", ">"), SyntaxType.DEF),
  DEF_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%def>", ""), SyntaxType.DEF),
  METHOD(false, SimpleScanner.makeBoundedScannerFactory("<%method ", ">"), SyntaxType.METHOD),
  METHOD_CLOSE(false, SimpleScanner.makeBoundedScannerFactory("</%method>", ""), SyntaxType.METHOD),
  EMIT(true, SimpleScanner.makeBoundedScannerFactory("<% ", "%>"), SyntaxType.EMIT),
  DOC(false, DocScanner.makeBoundedScannerFactory(), SyntaxType.DOC);

  private final static Map<SyntaxType, Set<PartitionDescriptor>> s_descriptorsByType =
      new EnumMap<SyntaxType, Set<PartitionDescriptor>>(SyntaxType.class);

  static
  {
      for (SyntaxType syntaxType: SyntaxType.values())
      {
          Set<PartitionDescriptor> descriptors = EnumSet.noneOf(PartitionDescriptor.class);
          s_descriptorsByType.put(syntaxType, descriptors);
          for (PartitionDescriptor partitionDescriptor: values())
          {
              if (partitionDescriptor.m_syntaxType == syntaxType)
              {
                  descriptors.add(partitionDescriptor);
              }
          }
      }
  }

  public static Set<PartitionDescriptor> getDescriptorsByType(SyntaxType p_syntaxType)
  {
      return s_descriptorsByType.get(p_syntaxType);
  }

  private PartitionDescriptor(
      boolean p_hasStrings,
      BoundedScannerFactory p_scannerBoundedScannerFactory,
      SyntaxType p_syntaxType)
  {
    this(
        new OpenClosePair(p_scannerBoundedScannerFactory, p_syntaxType),
        p_hasStrings,
        p_scannerBoundedScannerFactory, p_syntaxType);
  }

  private static class OpenClosePair
  {
      private final char[] open, close;
      public OpenClosePair(
          BoundedScannerFactory p_scannerBoundedScannerFactory, SyntaxType p_syntaxType)
      {
          BoundedScanner scanner = p_scannerBoundedScannerFactory.create(p_syntaxType);
          open = scanner.open();
          close = scanner.close();
      }
  }

  private PartitionDescriptor(
      OpenClosePair p_openClosePair,
      boolean p_hasStrings,
      BoundedScannerFactory p_scannerBoundedScannerFactory,
      SyntaxType p_syntaxType)
  {
      this(
          p_openClosePair.open,
          p_openClosePair.close,
          p_hasStrings,
          p_scannerBoundedScannerFactory,
          p_syntaxType);
  }

  private PartitionDescriptor(
      char[] p_open,
      char[] p_close,
      boolean p_hasStrings,
      BoundedScannerFactory p_scannerBoundedScannerFactory,
      SyntaxType p_syntaxType)
  {
    m_open = p_open;
    m_close = p_close;
    m_name = "__jamon_partition_" + name();
    m_token = new Token(m_name);
    m_hasStrings = p_hasStrings;
    m_scannerBoundedScannerFactory = p_scannerBoundedScannerFactory;
    m_syntaxType = p_syntaxType;
  }

  public ITokenScanner scanner()
  {
    return m_scannerBoundedScannerFactory.create(m_syntaxType);
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
  private final SyntaxType m_syntaxType;
}