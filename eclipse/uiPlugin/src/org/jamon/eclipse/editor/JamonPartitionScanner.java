package org.jamon.eclipse.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class JamonPartitionScanner implements IPartitionTokenScanner
{

  private static final char STRING_ESCAPE_CHAR = '\\';

  private static final String JAMON = IDocument.DEFAULT_CONTENT_TYPE;
  public static final IToken JAMON_LINE_TOKEN = new Token("__jamon_partition_line");
  static final IToken JAMON_TOKEN = new Token(JAMON);

  public static final String[] JAMON_PARTITION_TYPES;
  static
  {
    List<String> types = new ArrayList<String>();
    for (PartitionDescriptor pd : PartitionDescriptor.values())
    {
      types.add(pd.tokenname());
    }
    types.add(JAMON_LINE_TOKEN.getData().toString());
    JAMON_PARTITION_TYPES = types.toArray(new String[types.size()]);
  }

  private IDocument document;
  private int offset;
  private int tokenOffset;
  private int tokenLength;
  private int length;
  private IToken currentContent;
  private int limit;
  private char[][] lineDelimiters;
  private char[][] javaLineStart;

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

  public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset)
  {
    this.document = document;
    this.offset = offset;
    this.length = length;
    this.currentContent = tokenFor(contentType);
    this.limit = offset + length;
    this.lineDelimiters = new char[document.getLegalLineDelimiters().length][];
    this.javaLineStart = new char[lineDelimiters.length][];
    for (int i = 0; i < lineDelimiters.length; i++)
    {
        String s = document.getLegalLineDelimiters()[i];
        lineDelimiters[i] = s.toCharArray();
        javaLineStart[i] = (s + "%").toCharArray();
    }
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

  private boolean lookingAt(int off, char[] match)
  {
    for (int i = 0; i < match.length; ++i)
    {
      if (nextChar(i + off) != match[i])
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
      if (lookingAt(i, new char[] { '%' }))
      {
          System.err.println("Found lone % at " + i );
      }
      for (int j = 0; j < javaLineStart.length; j++)
      {
          if (lookingAt(i, javaLineStart[j]))
          {
              int end = processSimpleSection(lineDelimiters[j], i + javaLineStart[j].length);
              if (end < 0)
              {
                tokenLength = length - (i - offset);
                offset = limit;
              }
              else
              {
                tokenLength = end - i - lineDelimiters[j].length;
                offset = end - lineDelimiters[j].length;
              }
              tokenOffset = i;
              return JAMON_LINE_TOKEN;
          }
      }
      i++;
    }
    tokenLength = i - offset;
    tokenOffset = offset;
    offset = i;
    return JAMON_TOKEN;
  }

  private int processSection(PartitionDescriptor pd, int i)
  {
    if (pd.hasStrings())
    {
      return processSectionWithStringLiterals(pd, i);
    }
    else
    {
      return processSimpleSection(pd.close(), i);
    }
  }

  private void setTokenInfo(PartitionDescriptor pd, int i)
  {
    int end = processSection(pd, i + pd.open().length);
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

  private int processSectionWithStringLiterals(PartitionDescriptor pd, int start)
  {
    int i = start;
    boolean inString = false; // FIXME: need to figure this out if in mid-partition!
    int lastChar = ICharacterScanner.EOF;
    while (i < limit)
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
        if (lookingAt(i, pd.close()))
        {
          return i + pd.close().length;
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

  private int processSimpleSection(final char[] close, int start)
  {
    int i = start;
    while (i < limit)
    {
      if (lookingAt(i, close))
      {
        return i + close.length;
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
