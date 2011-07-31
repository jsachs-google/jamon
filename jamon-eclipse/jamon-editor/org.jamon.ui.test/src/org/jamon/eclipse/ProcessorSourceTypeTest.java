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
