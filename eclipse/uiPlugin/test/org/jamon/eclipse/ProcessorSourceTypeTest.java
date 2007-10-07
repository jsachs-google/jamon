package org.jamon.eclipse;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProcessorSourceTypeTest
{
    @Test public void testFromPreferenceValue()
    {
        assertEquals(
            ProcessorSourceType.EXTERNAL, ProcessorSourceType.fromPreferenceValue("EXTERNAL"));
    }

    @Test public void testFromUnknownPreferenceValue()
    {
        assertEquals(ProcessorSourceType.PLUGIN, ProcessorSourceType.fromPreferenceValue("FOO"));
    }

    @Test public void testFromNullPreferenceValue()
    {
        assertEquals(ProcessorSourceType.PLUGIN, ProcessorSourceType.fromPreferenceValue(null));
    }
}
