/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;


public class DocScanner extends AbstractScanner
    implements DisposableScanner, StyleProvider.SyntaxStyleChangeListener
{
    private final char[] m_open;
    private final char[] m_close;

  public DocScanner(StyleProvider p_styleProvider, String p_openTag, String p_closeTag)
  {
      super(p_styleProvider);
      m_open = p_openTag.toCharArray();
      m_close = p_closeTag.toCharArray();
      m_styleProvider.addSyntaxStyleChangeListener(SyntaxType.DOC_TAG, this);
      m_styleProvider.addSyntaxStyleChangeListener(SyntaxType.DOC_BODY, this);
      styleChanged();
  }

  public static DisposableScannerFactory makeDisposableScannerFactory()
  {
    return new DisposableScannerFactory() {
        @Override
        public DisposableScanner create(StyleProvider p_styleProvider, String p_open, String p_close)
        {
            return new DocScanner(p_styleProvider, p_open, p_close);
        }
    };
  }

  @Override
  public IToken nextToken()
  {
    if (offset >= limit)
    {
      return Token.EOF;
    }

    tokenOffset = offset;

    if (lookingAt(offset, m_open))
    {
      tokenLength = m_open.length;
      offset += tokenLength;
      return m_docTagToken;
    }

    if (lookingAt(limit - m_close.length, m_close))
    {
      if (offset == limit - m_close.length)
      {
        offset = limit;
        tokenLength = m_close.length;
        return m_docTagToken;
      }
      else
      {
        offset = limit - m_close.length;
        tokenLength = limit - m_close.length - tokenOffset;
        return m_docBodyToken;
      }
    }
    else
    {
      offset = limit;
      tokenLength = limit - tokenOffset;
      return m_docBodyToken;
    }

  }

  public IToken m_docTagToken;
  public IToken m_docBodyToken;

    @Override
    public void styleChanged()
    {
        m_docTagToken = makeToken(SyntaxType.DOC_TAG);
        m_docBodyToken = makeToken(SyntaxType.DOC_BODY);
    }

    @Override
    public void dispose()
    {
        m_styleProvider.removeSyntaxStyleChangeListener(SyntaxType.DOC_TAG, this);
        m_styleProvider.removeSyntaxStyleChangeListener(SyntaxType.DOC_BODY, this);
    }

}
