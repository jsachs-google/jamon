/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class JamonProjectPlugin extends AbstractUIPlugin
{
    // The shared instance.
    private static JamonProjectPlugin plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public JamonProjectPlugin()
    {
        super();
        plugin = this;
        try
        {
            resourceBundle = ResourceBundle
                    .getBundle("org.jamon.JamonProjectPluginResources");
        }
        catch (MissingResourceException x)
        {
            resourceBundle = null;
        }
    }

    /**
     * Returns the shared instance.
     */
    public static JamonProjectPlugin getDefault()
    {
        return plugin;
    }

    public static String getProxyMarkerType()
    {
        return getDefault().pluginId() + ".proxyMarker";
    }

    public static String getImplMarkerType()
    {
        return getDefault().pluginId() + ".implMarker";
    }

    public static String getJamonMarkerType()
    {
        return getDefault().pluginId() + ".jamonMarker";
    }

    public static String getProjectMarkerType()
    {
        return getDefault().pluginId() + ".projectMarker";
    }

    public static String getParentMarkerType()
    {
        return getDefault().pluginId() + ".parentMarker";
    }

    public void logInfo(String p_message)
    {
        getLog().log(new Status(IStatus.INFO, pluginId(), 0, p_message, null));
    }

    public void logError(Throwable p_error)
    {
        String message = p_error.getMessage();
        getLog().log(new Status(IStatus.ERROR, pluginId(), 0, message != null
            ? message
            : "", p_error));
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not
     * found.
     */
    public static String getResourceString(String key)
    {
        ResourceBundle bundle = JamonProjectPlugin.getDefault()
                .getResourceBundle();
        try
        {
            return (bundle != null) ? bundle.getString(key) : key;
        }
        catch (MissingResourceException e)
        {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }

    public String pluginId()
    {
        return getBundle().getSymbolicName();
    }
}
