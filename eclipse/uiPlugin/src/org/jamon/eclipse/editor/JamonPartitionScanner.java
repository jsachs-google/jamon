package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


/*
partition jamon:

find first tag
 if doc, switch to doc mode
 if emit or args or java, switch to string-handling mode
 consume everything until see a close tag or another open tag
 if another open tag, push current and recurse
 when find close tag, partition done

This doesn't quite work because we can't give nested partitions
but we allow nested tags

What are the partitions below?

<%method foo>

  <%args>
     String s = "<%/args>";
     int i;
  </%args>

  <& /a/b/c: "<%method>" &>
  <% "<&| foo: 3 &></&>" %>
<%
5
  %>
  <&| /a/c &>
      ...
  </&>

% int j = i;
<%java>
j++
</%java>

</%method>

What about:

ARGS: line 18-19
CALL: line 22
EMIT: line 23
CALL: line 24
JAVA: line 28
JAVA: line 30
DEFAULT: everywhere else

 */

public class JamonPartitionScanner implements IPartitionTokenScanner
{

  private static final char STRING_ESCAPE_CHAR = '\\';
  public static final String JAMON = IDocument.DEFAULT_CONTENT_TYPE;
  public static final String ARGS = "__jamon_partition_args";
  public static final String JAVA = "__jamon_partition_java";
  public static final String EMIT = "__jamon_partition_emit";
  public static final String CLASS = "__jamon_partition_class";
  public static final String DOC = "__jamon_partition_doc";
  public static final String ALIAS = "__jamon_partition_alias";
  public static final String IMPORT = "__jamon_partition_import";

  private static final IToken JAVA_TOKEN = new Token(JAVA);
  private static final IToken EMIT_TOKEN = new Token(EMIT);
  private static final IToken JAMON_TOKEN = new Token(JAMON);
  private static final IToken ARGS_TOKEN = new Token(ARGS);
  private static final IToken CLASS_TOKEN = new Token(CLASS);
  private static final IToken DOC_TOKEN = new Token(DOC);
  private static final IToken ALIAS_TOKEN = new Token(ALIAS);
  private static final IToken IMPORT_TOKEN = new Token(IMPORT);

  public static final String[] JAMON_PARTITION_TYPES = new String[] {
    JAMON_TOKEN.getData().toString(),
    ARGS_TOKEN.getData().toString(),
    JAVA_TOKEN.getData().toString(),
    EMIT_TOKEN.getData().toString(),
    CLASS_TOKEN.getData().toString(),
    DOC_TOKEN.getData().toString(),
    ALIAS_TOKEN.getData().toString(),
    IMPORT_TOKEN.getData().toString(),
  };

  private IDocument document;
  private int offset;
  private int tokenOffset;
  private int tokenLength;
  private int length;
  private IToken currentContent;
  private int limit;

  private static IToken tokenFor(String contentType)
  {
    if (contentType == null)
    {
      return JAMON_TOKEN;
    }
    else if (ARGS_TOKEN.getData().toString().equals(contentType))
    {
      return ARGS_TOKEN;
    }
    else if (JAVA_TOKEN.getData().toString().equals(contentType))
    {
      return JAVA_TOKEN;
    }
    else if (EMIT_TOKEN.getData().toString().equals(contentType))
    {
      return EMIT_TOKEN;
    }
    else if (CLASS_TOKEN.getData().toString().equals(contentType))
    {
      return CLASS_TOKEN;
    }
    else if (JAMON_TOKEN.getData().toString().equals(contentType))
    {
      return JAMON_TOKEN;
    }
    else if (ALIAS_TOKEN.getData().toString().equals(contentType))
    {
      return ALIAS_TOKEN;
    }
    else if (DOC_TOKEN.getData().toString().equals(contentType))
    {
      return DOC_TOKEN;
    }
    else if (IMPORT_TOKEN.getData().toString().equals(contentType))
    {
      return IMPORT_TOKEN;
    }
    else
    {
      throw new IllegalArgumentException("unknown content type " + contentType);
    }
  }

  private int endOffset;

  public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset)
  {
    this.document = document;
    this.offset = offset;
    this.length = length;
    this.endOffset = offset + length;
    this.currentContent = tokenFor(contentType);
    this.limit = offset + length;
  }

  public int getTokenLength()
  {
    return tokenLength;
  }

  public int getTokenOffset()
  {
    return tokenOffset;
  }

  private int nextChar(int i)
  {
    try
    {
      return document.getChar(i);
    }
    catch (BadLocationException e)
    {
      return ICharacterScanner.EOF;
    }
  }

  private boolean lookingAt(int off, String match)
  {
    for (int i = 0; i < match.length(); ++i)
    {
      if (nextChar(i + off) != match.charAt(i))
      {
        return false;
      }
    }
    return true;
  }
  
  private static enum Section {
    ARGS("<%args>", "</%args>", ARGS_TOKEN, true), 
    JAVA("<%java>", "</%java>", JAVA_TOKEN, true),
    EMIT("<% ", "%>", EMIT_TOKEN, true),
    CLASS("<%class>", "</%class>", CLASS_TOKEN, true),
    ALIAS("<%alias>", "</%alias>", ALIAS_TOKEN, true),
    IMPORT("<%import>", "</%import>", IMPORT_TOKEN, true),
    DOC("<%doc>", "</%doc>", DOC_TOKEN, false);
    
    private Section(String p_open, String p_close, IToken p_token, boolean p_hasStrings) {
      m_open = p_open;
      m_close = p_close;
      m_token = p_token;
      m_hasStrings = p_hasStrings;
    }
    
    public IToken token() {
      return m_token;
    }
    
    public String close() {
      return m_close;
    }
    public String open() {
      return m_open;
    }
    
    public boolean hasStrings()
    {
      return m_hasStrings;
    }
    
    private final String m_open;
    private final String m_close;
    private final IToken m_token;
    private final boolean m_hasStrings;
  }

  private IToken processDefault()
  {
    int i = offset;
    while (i < limit)
    {
      for (Section s : Section.values()) 
      {
        if (lookingAt(i, s.open())) 
        {
          setTokenInfo(s, i);
          return s.token();
        }
      }
      i++;
    }
    tokenLength = i - offset + 1;
    tokenOffset = offset;
    offset = i;
    return JAMON_TOKEN;
  }
  
  private int processSection(Section s, int i)
  {
    if (s.hasStrings())
    {
      return processSectionWithStringLiterals(i, s.close());
    }
    else
    {
      return processSimpleSection(i, s.close());
    }
  }

  private void setTokenInfo(Section s, int i)
  {
    int end = processSection(s, i);
    if (end < 0)
    {
      tokenLength = length - (i - offset);
      offset = limit;
    }
    else
    {
      tokenLength = end - i + 1;
      offset = end;
    }
    tokenOffset = i;
  }

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    else if (currentContent == JAMON_TOKEN)
    {
      return processDefault();
    }
    else
    {
      return resumedNextToken();
    }
  }

  private IToken resumedNextToken()
  {
    for (Section s : Section.values())
    {
      if (currentContent == s.token())
      {
        int end = processSectionWithStringLiterals(offset, s.close());
        if (end < 0)
        {
          // didn't see close
          return currentContent;
        }
        else
        {
          // found it
          tokenLength = end - offset + 1;
          tokenOffset = end;
          return JAMON_TOKEN;
        }
      }
    }
    throw new IllegalStateException();
  }

  private int processSectionWithStringLiterals(int start, String endToken)
  {
    int i = start;
    boolean inString = false; // FIXME: need to figure this out if in mid-partition!
    int lastChar = ICharacterScanner.EOF;
    while (i < endOffset)
    {
      int c = nextChar(i);
      if (c == '"')
      {
        if (inString)
        {
          if (lastChar != STRING_ESCAPE_CHAR)
          {
            inString = false;
          }
        }
        else
        {
          inString = true;
        }
      }
      else if (! inString)
      {
        if (lookingAt(i, endToken))
        {
          return i + endToken.length();
        }
      }
      else
      {
        if (lastChar == STRING_ESCAPE_CHAR && c == STRING_ESCAPE_CHAR)
        {
          lastChar = ICharacterScanner.EOF;
        }
      }
      i++;
      lastChar = c;
    }
    return -1;
  }

  private int processSimpleSection(int start, String endToken)
  {
    int i = start;
    while (i < endOffset)
    {
      if (lookingAt(i, endToken))
      {
        return i + endToken.length();
      }
      i++;
    }
    return -1;
  }

  public void setRange(IDocument document, int offset, int length)
  {
    setPartialRange(document, offset, length, null, -1);
  }


}
