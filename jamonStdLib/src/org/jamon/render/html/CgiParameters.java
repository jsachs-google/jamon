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
 * Contributor(s):
 */

package org.jamon.render.html;

import java.util.ArrayList;
import java.util.List;

public class CgiParameters
{
    public CgiParameters add( String p_name, String p_value )
    {
        m_inputs.add( new Input( p_name, p_value ) );
        return this;
    }

    public Input[] getInputs()
    {
        return m_inputs.toArray( new Input[0] );
    }

    private final List<Input> m_inputs = new ArrayList<Input>();
}
