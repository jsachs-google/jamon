package org.jamon.tests.templates.escape;

import org.jamon.escape.Html;

public class HtmlTest
    extends TestBase
{
    public void testEmpty()
        throws Exception
    {
        new Html(getTemplateManager())
            .render(getWriter(), new Fragment(""));
        checkOutput("");
    }

    public void testSimple()
        throws Exception
    {
        new Html(getTemplateManager())
            .render(getWriter(), new Fragment("hello"));
        checkOutput("hello");
    }

    public void testEscaping()
        throws Exception
    {
        new Html(getTemplateManager())
            .render(getWriter(), new Fragment("<& &gt; &>!\"'"));
        checkOutput("&lt;&amp; &amp;gt; &amp;&gt;!\"'");
    }

}