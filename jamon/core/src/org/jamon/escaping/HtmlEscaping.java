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
 * The Initial Developer of the Original Code is Luis O'Shea.  Portions
 * created by Luis O'Shea are Copyright (C) 2002 Luis O'Shea.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */

package org.jamon.escaping;

import java.io.Writer;
import java.io.IOException;

public class HtmlEscaping
    extends AbstractCharacterEscaping
{

    HtmlEscaping()
    {
        // package scope constructor
    }

    protected void write(char p_char, Writer p_writer)
        throws IOException
    {
        switch (p_char)
        {
          case '<': p_writer.write("&lt;"); break;
          case '>': p_writer.write("&gt;"); break;
          case '&': p_writer.write("&amp;"); break;
          default: p_writer.write(p_char);
        }
    }

}