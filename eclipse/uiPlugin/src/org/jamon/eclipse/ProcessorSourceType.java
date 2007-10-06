package org.jamon.eclipse;

public enum ProcessorSourceType {
    PLUGIN, WORKSPACE, EXTERNAL, VARIABLE;

    public String preferenceValue() {
        return name();
    }

    public static ProcessorSourceType fromPreferenceValue(String preferenceValue) {
        ProcessorSourceType processorSourceType = valueOf(preferenceValue);
        return processorSourceType == null ? PLUGIN : processorSourceType;
    }
}
