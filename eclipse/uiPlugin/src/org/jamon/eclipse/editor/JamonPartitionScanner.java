package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

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

  private static final String JAMON = IDocument.DEFAULT_CONTENT_TYPE;
  private static final IToken JAMON_TOKEN = new Token(JAMON);

  public static final String[] JAMON_PARTITION_TYPES;
  static 
  {
    List<String> types = new ArrayList<String>();
    for (PartitionDescriptor pd : PartitionDescriptor.values())
    {
      types.add(pd.tokenname());
    }
    JAMON_PARTITION_TYPES = types.toArray(new String[types.size()]);
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
    if (contentType == null || contentType.equals(JAMON_TOKEN.getData()))
    {
      return JAMON_TOKEN;
    }
    for (PartitionDescriptor pd : PartitionDescriptor.values())
    {
      if (pd.tokenname().equals(contentType))
      {
        return pd.token();
      }
    }
    throw new IllegalArgumentException("unknown content type " + contentType);
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
  
  private IToken processDefault()
  {
    int i = offset;
    while (i < limit)
    {
      for (PartitionDescriptor s : PartitionDescriptor.values()) 
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
  
  private int processSection(PartitionDescriptor s, int i)
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

  private void setTokenInfo(PartitionDescriptor s, int i)
  {
    int end = processSection(s, i);
    if (end < 0)
    {
      tokenLength = length - (i - offset);
      offset = limit;
    }
    else
    {
      tokenLength = end - i;
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
    for (PartitionDescriptor s : PartitionDescriptor.values())
    {
      if (currentContent == s.token())
      {
        int end = processSection(s, offset);
        if (end < 0)
        {
          // didn't see close
          tokenOffset = offset; // partitionOffset;
          tokenLength = limit - tokenOffset + 1;
          offset = limit;
          return currentContent;
        }
        else
        {
          // found it
          tokenOffset = offset; // partitionOffset;
          tokenLength = end - tokenOffset + 1;
          offset = end;
          return currentContent; // JAMON_TOKEN;
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
