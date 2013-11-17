/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.eclipse;

/**
 * A location in a file.  The begining of the file has line and column number 1
 */
public class Location implements Comparable<Location>
{
    public Location(int p_line, int p_column)
    {
        m_line = p_line;
        m_column = p_column;
    }

    public int getLine() { return m_line; }
    public int getColumn() { return m_column; }

    @Override
    public int compareTo(Location p_other)
    {
        int lineDelta = getLine() - p_other.getLine();
        return lineDelta == 0 ? getColumn() - p_other.getColumn() : lineDelta;
    }

    private final int m_line;
    private final int m_column;
}
