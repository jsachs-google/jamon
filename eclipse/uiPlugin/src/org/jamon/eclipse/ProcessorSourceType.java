package org.jamon.eclipse;

public enum ProcessorSourceType {
    WORKSPACE, EXTERNAL;

    public String preferenceValue() {
        return name();
    }

    public static ProcessorSourceType fromPreferenceValue(String preferenceValue) {
        return valueOf(preferenceValue);
    }
}
