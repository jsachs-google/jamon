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

import org.jamon.util.StringUtils;

/**
 * A standard implementation of the {@link TemplateManager} interface.
 * The <code>BasicTemplateManager</code> is geared towards production
 * deployment; it is designed for performance. It will <b>NOT</b>
 * dynamically examine or recompile template sources.
 *
 * <code>BasicTemplateManager</code> instances are thread-safe.  In
 * your applications, you generally want exactly one instance of a
 * BasicTemplateManager (i.e. a singleton), so consider using {@link
 * TemplateSource}
 **/

public class BasicTemplateManager
    implements TemplateManager
{
    /**
     * Creates a new <code>BasicTemplateManager</code> using a default
     * <code>ClassLoader</code>.
     **/
    public BasicTemplateManager()
    {
        this(null);
    }

    /**
     * Creates a new <code>BasicTemplateManager</code> from a
     * specified <code>ClassLoader</code>.
     *
     * @param p_classLoader the <code>ClassLoader</code> to use to
     * load templates.
     **/
    public BasicTemplateManager(ClassLoader p_classLoader)
    {
        m_classLoader = p_classLoader == null
            ? getClass().getClassLoader()
            : p_classLoader;
    }

    public AbstractTemplateProxy.Intf constructImpl
        (AbstractTemplateProxy p_proxy)
    {
        return p_proxy.constructImpl();
    }

    /**
     * Given a template path, return a proxy for that template.
     *
     * @param p_path the path to the template
     *
     * @return a <code>Template</code> proxy instance
     **/
    public AbstractTemplateProxy constructProxy(String p_path)
    {
        try
        {
            return getProxyClass(p_path)
                .getConstructor(new Class [] { TemplateManager.class })
                .newInstance(new Object [] { this });
        }
        catch (ClassNotFoundException e)
        {
            throw new UnknownTemplateException(p_path);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new JamonRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractTemplateProxy> getProxyClass(String p_path)
        throws ClassNotFoundException
    {
        return (Class<? extends AbstractTemplateProxy>) m_classLoader.loadClass
            (StringUtils.templatePathToClassName(p_path));
    }

    private final ClassLoader m_classLoader;
}
