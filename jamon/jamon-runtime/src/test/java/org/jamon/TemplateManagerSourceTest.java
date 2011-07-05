/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2003 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon;

import org.jamon.AbstractTemplateProxy;
import org.jamon.AbstractTemplateProxy.Intf;
import org.jamon.TemplateManager;
import org.jamon.TemplateManagerSource;

import junit.framework.TestCase;

public class TemplateManagerSourceTest
    extends TestCase
{
    public void testSetSource()
    {
        final TemplateManager tm = new TestTemplateManager();
        TemplateManagerSource.setTemplateManagerSource
            (new TemplateManagerSource()
                {
                @Override public TemplateManager getTemplateManagerForPath
                        (String p_path)
                    {
                        return tm;
                    }
                });
        assertSame(tm, TemplateManagerSource.getTemplateManagerFor(""));
    }

    public void testSetManager()
    {
        TemplateManager tm = new TestTemplateManager();
        TemplateManagerSource.setTemplateManager(tm);
        assertSame(tm, TemplateManagerSource.getTemplateManagerFor(""));
    }

    private static class TestTemplateManager extends AbstractTemplateManager
    {

        @Override
        public AbstractTemplateProxy constructProxy(String p_path)
        {
            return null;
        }

        @Override
        protected Intf constructImplFromReplacedProxy(AbstractTemplateProxy p_replacedProxy)
        {
            return null;
        }
    }
}
