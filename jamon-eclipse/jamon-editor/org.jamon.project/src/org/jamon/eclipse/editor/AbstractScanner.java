/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jamon.eclipse.editor.preferences.Style;
import org.jamon.eclipse.editor.preferences.StyleProvider;
import org.jamon.eclipse.editor.preferences.SyntaxType;

public abstract class AbstractScanner implements ITokenScanner
{
    protected AbstractScanner(StyleProvider p_styleProvider)
    {
        m_styleProvider = p_styleProvider;
    }

    @Override
    public int getTokenLength()
    {
        return tokenLength;
    }

    @Override
    public int getTokenOffset()
    {
        return tokenOffset;
    }

    protected boolean lookingAt(int off, char[] match)
    {
        for (int i = 0; i < match.length; ++i)
        {
            if (charAt(i + off) != match[i]) { return false; }
        }
        return true;
    }

    protected int charAt(int i)
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

    @Override
    public void setRange(IDocument p_document, int p_offset, int p_length)
    {
        this.document = p_document;
        this.initialOffset = p_offset;
        this.offset = p_offset;
        this.length = p_length;
        this.limit = offset + length;
    }

    protected Token makeToken(SyntaxType p_syntaxType)
    {
        Style style = m_styleProvider.getStyle(p_syntaxType);
        return new Token(new TextAttribute(
            JamonColorProvider.instance().getColor(style.getForeground()),
            JamonColorProvider.instance().getColor(style.getBackground()),
            style.getSwtStyle()));
    }

    protected IDocument document;
    protected int offset;
    protected int initialOffset;
    protected int length;
    protected int tokenOffset;
    protected int tokenLength;
    protected int limit;
    protected final StyleProvider m_styleProvider;

}
