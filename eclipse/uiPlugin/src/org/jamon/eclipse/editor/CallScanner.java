package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;
import org.jamon.eclipse.editor.preferences.StyleProvider.SyntaxStyleChangeListener;

public class CallScanner extends AbstractScanner
    implements DisposableScanner, SyntaxStyleChangeListener
{
  private final JamonJavaCodeScanner javaScanner;
  private final char[] open;
  private final char[] close;

  public static DisposableScannerFactory FACTORY = new DisposableScannerFactory() {
      public DisposableScanner create(StyleProvider p_styleProvider, String p_openTag, String p_closeTag)
      {
          return new CallScanner(p_styleProvider, p_openTag, p_closeTag);
      }
    };

  public CallScanner(StyleProvider p_styleProvider, String p_openTag, String p_closeTag)
  {
      super(p_styleProvider);
      javaScanner = new JamonJavaCodeScanner(JamonColorProvider.instance(), BG);
      open = p_openTag.toCharArray();
      close = p_closeTag.toCharArray();
      styleChanged();
      p_styleProvider.addSyntaxStyleChangeListener(SyntaxType.CALL, this);
      p_styleProvider.addSyntaxStyleChangeListener(SyntaxType.CALL_PATH, this);
  }

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    tokenOffset = offset;

    if (lookingAt(offset,open))
    {
      tokenLength = open.length;
      offset += tokenLength;
      return m_tagToken;
    }

    else if (sawPath)
    {
      if (lookingAt(limit-close.length, close))
      {
        if (offset == limit - close.length)
        {
          tokenLength = close.length;
          offset += close.length;
          return m_tagToken;
        }
      }

      {
        final IToken tok = javaScanner.nextToken();
        tokenLength = javaScanner.getTokenLength();
        offset  = javaScanner.getTokenOffset();
        offset += tokenLength;
        return tok;
      }
    }

    else if (isWhitespace(charAt(offset)))
    {
      offset++;
      while (offset < limit && isWhitespace(charAt(offset)))
      {
        offset++;
      }
      tokenLength = offset - tokenOffset;
      return m_whitespaceToken;
    }

    else
    {
      offset++;
      while (offset < limit)
      {
        int c = charAt(offset);
        if (isWhitespace(c) || c == ':' || c == ';')
        {
          break;
        }
        offset++;
      }
      tokenLength = offset - tokenOffset;
      sawPath = true;
      javaScanner.setRange(document, offset, Math.max(limit - offset - 2, 0));
      return m_pathToken;
    }
  }

  private boolean isWhitespace(int ch)
  {
    return Character.isWhitespace(ch);
  }

  @Override
  public void setRange(IDocument p_document, int p_offset, int p_length)
  {
    super.setRange(p_document, p_offset, p_length);
    this.sawPath = false;
  }

  static IToken token(RGB fg, RGB bg, int style)
  {
    return new Token(new TextAttribute(JamonColorProvider.instance().getColor(fg),
                                       JamonColorProvider.instance().getColor(bg),
                                       style));
  }

  private IToken m_tagToken, m_pathToken, m_whitespaceToken;

  private static RGB BG = null; //FIXME - REMOVE
  private boolean sawPath;

    public void styleChanged()
    {
        m_tagToken = makeToken(SyntaxType.CALL);
        m_pathToken = makeToken(SyntaxType.CALL_PATH);
        TextAttribute textAttribute = (TextAttribute) m_tagToken.getData();
        m_whitespaceToken = new Token(new TextAttribute(
            textAttribute.getForeground(), textAttribute.getBackground(), SWT.NONE));
    }

    public void dispose()
    {
        m_styleProvider.removeSyntaxStyleChangeListener(SyntaxType.CALL, this);
        m_styleProvider.removeSyntaxStyleChangeListener(SyntaxType.CALL_PATH, this);
    }
}
