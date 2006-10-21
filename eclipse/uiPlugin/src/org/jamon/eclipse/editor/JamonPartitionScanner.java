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

	public static final String JAMON = IDocument.DEFAULT_CONTENT_TYPE;
	public static final String ARGS = "__jamon_partition_args";
	
	private static final IToken ARGS_TOKEN = new Token(ARGS);
	private static final IToken CALL_TOKEN = new Token("__jamon_partition_call");
	private static final IToken EMIT_TOKEN = new Token("__jamon_partition_emit");
	private static final IToken JAVA_TOKEN = new Token("__jamon_partition_java");
	private static final IToken JAMON_TOKEN = new Token(JAMON);
	
	public static final String[] JAMON_PARTITION_TYPES = new String[] {
		JAMON_TOKEN.getData().toString(),
		ARGS_TOKEN.getData().toString(),
	};
	
	private IDocument document;
	private int offset;
	private int tokenOffset;
	private int tokenLength;
	private int length;
	private IToken currentContent;
	private int partitionOffset;
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
		else if (CALL_TOKEN.getData().toString().equals(contentType)) 
		{
			return CALL_TOKEN;
		}
		else if (EMIT_TOKEN.getData().toString().equals(contentType))
		{
			return EMIT_TOKEN;
		}
		else if (JAVA_TOKEN.getData().toString().equals(contentType)) 
		{
			return JAVA_TOKEN;
		}
		else if (JAMON_TOKEN.getData().toString().equals(contentType))
		{
			return JAMON_TOKEN;
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
		this.partitionOffset = partitionOffset;
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
			if (lookingAt(i, "<%args>")) 
			{
				int end = processArgs(i);
				if (end < 0) 
				{
					// no </%args>
					tokenLength = length - (i - offset);
					offset = limit;
				}
				else 
				{
					// found </%args>
					tokenLength = end - i + 1;
					offset = end;
				}
				tokenOffset = i;
				return ARGS_TOKEN;
			}
			/*
			else if (lookingAt(i, "<%java>")) {
				offset = i;
				return JAVA_TOKEN;
			}
			*/
			i++;
		}
		tokenLength = i - offset + 1;
		tokenOffset = offset;
		offset = i;
		return JAMON_TOKEN;
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
			return xnextToken();
			/*
			tokenOffset = 0;
			tokenLength = length;
			offset += length;
			return JAMON_TOKEN;
			*/
		}
	}
	
	public IToken xnextToken() 
	{
		if (currentContent == ARGS_TOKEN) {
			int end = processArgs(offset);
			if (end < 0) 
			{
				// didn't see </%args>
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
		/*
		else if (currentContent == JAVA_TOKEN) {
			return processJava();
		}
		*/
		else
		{
			throw new IllegalStateException();
		}
	}
	
	IToken processJava() 
	{
		return null;
	}

	private int processArgs(int start) 
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
					if (lastChar != '\\') 
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
				if (lookingAt(i, "</%args>")) 
				{
					return i + 7;
				}
			}
			else
			{
				if (lastChar == '\\' && c == '\\') 
				{
					lastChar = ICharacterScanner.EOF;
				}
			}
			i++;
			lastChar = c;
		}
		return -1;
	}

	public void setRange(IDocument document, int offset, int length) 
	{
		setPartialRange(document, offset, length, null, -1);
	}


}
