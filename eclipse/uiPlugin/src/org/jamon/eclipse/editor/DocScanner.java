package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jamon.eclipse.editor.preferences.StyleProvider;


//FIXME - use configurable styles
public class DocScanner extends AbstractScanner implements BoundedScanner
{
    private final char[] m_open;
    private final char[] m_close;

  public DocScanner(StyleProvider p_styleProvider, String p_openTag, String p_closeTag)
  {
      m_open = p_openTag.toCharArray();
      m_close = p_closeTag.toCharArray();
  }

  public static BoundedScannerFactory makeBoundedScannerFactory()
  {
    return new BoundedScannerFactory() {
        public BoundedScanner create(StyleProvider p_styleProvider, String p_open, String p_close)
        {
            return new DocScanner(p_styleProvider, p_open, p_close);
        }
    };
  }

  public char[] close() { return m_close; }
  public char[] open() { return m_open; }

  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }

    tokenOffset = offset;

    if (offset == initialOffset)
    {
      Assert.isTrue(lookingAt(offset, m_open));
      tokenLength = m_open.length;
      offset += tokenLength;
      return TAG;
    }

    if (lookingAt(limit - m_close.length, m_close))
    {
      if (offset == limit - m_close.length)
      {
        offset = limit;
        tokenLength = m_close.length;
        return TAG;
      }
      else
      {
        offset = limit - m_close.length;
        tokenLength = limit - m_close.length - tokenOffset;
        return DOC;
      }
    }
    else
    {
      offset = limit;
      tokenLength = limit - tokenOffset;
      return DOC;
    }

  }


  public final static IToken TAG = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(127, 127, 127)), null, SWT.BOLD));
  public final static IToken DOC = new Token(new TextAttribute(JamonColorProvider.instance().getColor(new RGB(112, 112, 112)), null, SWT.ITALIC));
}
