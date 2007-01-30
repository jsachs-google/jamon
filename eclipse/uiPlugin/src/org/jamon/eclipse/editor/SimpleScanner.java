package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.jamon.eclipse.editor.preferences.Style;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public class SimpleScanner extends AbstractScanner
    implements DisposableScanner, StyleProvider.SyntaxStyleChangeListener
{
    public final static DisposableScannerFactory makeFactory(final SyntaxType p_syntaxType)
    {
        return new DisposableScannerFactory() {
            public DisposableScanner create(
                StyleProvider p_styleProvider,
                String p_openTag,
                String p_closeTag)
            {
                return new SimpleScanner(p_openTag, p_closeTag, p_syntaxType, p_styleProvider);
            }
        };
    }

    public static final String THEME_BASE = "org.jamon.theme.";

    static RGB fg(String propertyBase)
    {
        return color(propertyBase + ".foreground", JamonColorProvider.DEFAULT);
    }

    static RGB bg(String propertyBase)
    {
        return color(propertyBase + ".background", JamonColorProvider.DEFAULT);
    }

    private static RGB color(String propertyName, RGB defaultValue)
    {
        RGB themeRgb = themeColor(propertyName);
        return themeRgb != null ? themeRgb : defaultValue;
    }

    private static RGB themeColor(String propertyName) {
        return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().getRGB(
            THEME_BASE + propertyName);
    }

    private final char[] open;
    private final char[] close;
    private IToken tagToken;
    private JamonJavaCodeScanner m_javaScanner;
    protected final SyntaxType m_syntaxType;

    @Deprecated protected SimpleScanner(String p_openTag, String p_closeTag, String propertyBase)
    {
        super(null);
        open = p_openTag.toCharArray();
        close = p_closeTag.toCharArray();
        RGB bgColor = bg(propertyBase);
        tagToken = new Token(new TextAttribute(
            JamonColorProvider.instance().getColor(fg(propertyBase)),
            JamonColorProvider.instance().getColor(bgColor),
            SWT.NORMAL));
        m_javaScanner  = new JamonJavaCodeScanner(JamonColorProvider.instance(), bgColor);
        m_syntaxType = null;
    }

    protected SimpleScanner(
        String p_openTag, String p_closeTag, SyntaxType p_syntaxType, StyleProvider p_styleProvider)
    {
        super(p_styleProvider);
        open = p_openTag.toCharArray();
        close = p_closeTag.toCharArray();
        m_syntaxType = p_syntaxType;
        styleChanged();
        p_styleProvider.addSyntaxStyleChangeListener(p_syntaxType, this);
    }

    public void dispose()
    {
        m_styleProvider.removeSyntaxStyleChangeListener(m_syntaxType, this);
    }

    public void setTagTokenStyle(Style style)
    {
        tagToken = new Token(new TextAttribute(
            JamonColorProvider.instance().getColor(style.getForeground()),
            JamonColorProvider.instance().getColor(style.getBackground()),
            style.getSwtStyle()));
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
          tagToken = makeToken(m_syntaxType);
          m_javaScanner = new JamonJavaCodeScanner(
              JamonColorProvider.instance(), m_styleProvider.getStyle(m_syntaxType).getBackground());
    }
    
    @Override public void setRange(IDocument p_document, int p_offset, int p_length)
    {
        super.setRange(p_document, p_offset, p_length);
        while (! lookingAt(initialOffset, open))
        {
            initialOffset--;
            offset--;
            length++;
            limit++;
        }
    }
}
