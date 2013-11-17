/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import junit.framework.TestCase;

public class ProcessorSourceTypeTest extends TestCase
{
    public void testFromPreferenceValue()
    {
        assertEquals(
            ProcessorSourceType.EXTERNAL, ProcessorSourceType.fromPreferenceValue("EXTERNAL"));
    }

    public void testFromUnknownPreferenceValue()
    {
        assertEquals(ProcessorSourceType.PLUGIN, ProcessorSourceType.fromPreferenceValue("FOO"));
    }

    public void testFromNullPreferenceValue()
    {
        assertEquals(ProcessorSourceType.PLUGIN, ProcessorSourceType.fromPreferenceValue(null));
    }
}
