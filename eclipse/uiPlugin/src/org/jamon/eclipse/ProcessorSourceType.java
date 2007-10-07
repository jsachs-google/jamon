package org.jamon.eclipse;

public enum ProcessorSourceType {
    PLUGIN, WORKSPACE, EXTERNAL, VARIABLE;

    public String preferenceValue() {
        return name();
    }

    public static ProcessorSourceType fromPreferenceValue(String preferenceValue) {
        try {
            return valueOf(preferenceValue);
        }
        catch (Exception e) {
            return PLUGIN;
        }
    }
}
