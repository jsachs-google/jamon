package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class CallScanner extends AbstractScanner implements BoundedScanner
{
  private final JamonJavaCodeScanner javaScanner;
  private final char[] open;
  private final char[] close;

  public static BoundedScannerFactory makeBoundedScannerFactory(final boolean p_withContent)
  {
      return new BoundedScannerFactory() {
        public BoundedScanner create()
        {
            return new CallScanner(p_withContent);
        }
      };
  }

  public CallScanner(boolean p_withContent) {
    javaScanner = new JamonJavaCodeScanner(JamonColorProvider.instance(), BG);
    close = "&>".toCharArray();
    open = (p_withContent ? "<&|" : "<&").toCharArray();
  }

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
      tokenLength = 2;
      offset += 2;
      return TAG;
    }

    else if (sawPath)
    {
      if (lookingAt(limit-2, close))
      {
        if (offset == limit - 2)
        {
          tokenLength = 2;
          offset += 2;
          return TAG;
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
      return WHITESPACE;
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
      javaScanner.setRange(document, offset, limit - offset - 2);
      return PATH;
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

  static final RGB BG = new RGB(240, 240, 240);
  static final IToken TAG = token(new RGB(127, 31, 15), BG, SWT.ITALIC);
  static final IToken PATH = token(new RGB(127, 31, 15), BG, SWT.ITALIC | SWT.BOLD);
  static final IToken DEFAULT = token(new RGB(0, 0, 0), BG, SWT.NORMAL);
  static final IToken WHITESPACE = token(JamonColorProvider.DEFAULT, BG, SWT.NORMAL);
  private boolean sawPath;

}
