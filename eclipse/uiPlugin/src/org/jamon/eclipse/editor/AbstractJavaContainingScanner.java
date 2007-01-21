package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.editor.preferences.Style;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public abstract class AbstractJavaContainingScanner extends AbstractScanner
    implements BoundedScanner, StyleProvider.SytnaxStyleChangeListener
{
    protected AbstractJavaContainingScanner(
        String p_openTag, String p_closeTag, SyntaxType p_syntaxType, StyleProvider p_styleProvider)
    {
        open = p_openTag.toCharArray();
        close = p_closeTag.toCharArray();
        m_syntaxType = p_syntaxType;
        m_styleProvider = p_styleProvider;
        styleChanged();
        p_styleProvider.addSyntaxStyleChangeListener(p_syntaxType, this);
    }

    public void setTagTokenStyle(Style style)
    {
        tagToken = new Token(new TextAttribute(
            JamonColorProvider.instance().getColor(style.getForeground()),
            JamonColorProvider.instance().getColor(style.getBackground()),
            style.getSwtStyle()));
    }

    @Deprecated protected AbstractJavaContainingScanner(
        String p_openTag, String p_closeTag, RGB fgColor, RGB bgColor) {
        open = p_openTag.toCharArray();
        close = p_closeTag.toCharArray();
        tagToken = new Token(new TextAttribute(
            JamonColorProvider.instance().getColor(fgColor),
            JamonColorProvider.instance().getColor(bgColor),
            SWT.NORMAL));
        m_javaScanner  = new JamonJavaCodeScanner(JamonColorProvider.instance(), bgColor);
        m_syntaxType = null;
        m_styleProvider = null;
    }

  private final char[] open;
  private final char[] close;
  private IToken tagToken;
  private JamonJavaCodeScanner m_javaScanner;
  private final SyntaxType m_syntaxType;
  private final StyleProvider m_styleProvider;

  public char[] close()
  {
    return close;
  }

  public char[] open()
  {
    return open;
  }

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }
    tokenOffset = offset;

    if (initialOffset == offset)
    {
      Assert.isTrue(lookingAt(offset, open));
      tokenLength = open.length;
      offset += open.length;
      m_javaScanner.setRange(document, offset, limit - offset);
      return tagToken;
    }

    else if (lookingAt(limit-close.length, close) && (offset == limit - close.length))
    {
      tokenLength = close.length;
      offset += close.length;
      return tagToken;
    }
    else
    {
      final IToken tok = m_javaScanner.nextToken();
      tokenLength = m_javaScanner.getTokenLength();
      offset  = m_javaScanner.getTokenOffset();
      offset += tokenLength;
      return tok;
    }
  }

  public void styleChanged()
  {
      Style style = m_styleProvider.getStyle(m_syntaxType);
      setTagTokenStyle(style);
      m_javaScanner = new JamonJavaCodeScanner(JamonColorProvider.instance(), style.getBackground());
  }
}
