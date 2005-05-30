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
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */

package org.jamon.node;

import java.util.Iterator;

public abstract class AbstractNode
{
    /**
     * @param p_location The location of this node
     **/

    protected AbstractNode(Location p_location)
    {
        if ((m_location = p_location) == null)
            throw new NullPointerException();
    }

    public final Location getLocation()
    {
        return m_location;
    }

    private final Location m_location;

    @Override public boolean equals(Object p_obj)
    {
        return p_obj != null
            && getClass().isInstance(p_obj)
            && m_location.equals(((AbstractNode) p_obj).m_location);
    }

    @Override public int hashCode()
    {
        return m_location.hashCode();
    }

    public abstract void apply(Analysis p_analysis);
    
    protected void propertiesToString(StringBuffer p_buffer)
    {
        p_buffer.append(m_location.toString());
    }

    @Override final public String toString()
    {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        buffer.append("{");
        propertiesToString(buffer);
        buffer.append("}");
        return buffer.toString();
    }

    protected static void addProperty(
        StringBuffer p_buffer,
        String p_label,
        char p_char)
    {
        p_buffer.append(", ");
        p_buffer.append(p_label);
        p_buffer.append(": ");
        p_buffer.append(p_char);
    }

    protected static void addProperty(
        StringBuffer p_buffer,
        String p_label,
        Object p_obj)
    {
        p_buffer.append(", ");
        p_buffer.append(p_label);
        p_buffer.append(": ");
        p_buffer.append(p_obj.toString());
    }

    protected static void addPropertyList(
        StringBuffer p_buffer,
        String p_name,
        Iterator<AbstractNode> p_properties)
    {
        p_buffer.append(", ");
        p_buffer.append(p_name);
        p_buffer.append(": [");
        while (p_properties.hasNext())
        {
            p_buffer.append(p_properties.next().toString());
            if (p_properties.hasNext())
            {
                p_buffer.append(", ");
            }
        }
        p_buffer.append("]");
    }

}
