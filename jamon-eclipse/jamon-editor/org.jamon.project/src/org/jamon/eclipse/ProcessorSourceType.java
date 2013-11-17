/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
