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
 * The Original Code is Jamon code, released October, 2002.
 *
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2002 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon.codegen;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jamon.JamonException;
import org.jamon.util.StringUtils;

public abstract class AbstractStandardUnitInfo extends AbstractUnitInfo
{
    public AbstractStandardUnitInfo(String p_name)
    {
        super(p_name);
    }

    public void addFarg(String p_name, String p_type)
    {
        addRequiredArg(p_name, p_type);
        m_fargs.add(p_name);
    }

    public void addOptionalArg(String p_name, String p_type, String p_default)
    {
        m_optionalArgs.add(new OptionalArgument(p_name, p_type, p_default));
    }

    public Iterator getOptionalArgs()
    {
        return m_optionalArgs.iterator();
    }

    public boolean hasOptionalArgs()
    {
        return !m_optionalArgs.isEmpty();
    }

    public Iterator getFargNames()
    {
        return m_fargs.iterator();
    }

    private final List m_requiredArgs = new LinkedList();
    private final List m_optionalArgs = new LinkedList();
    private final List m_fargs = new LinkedList();
}
