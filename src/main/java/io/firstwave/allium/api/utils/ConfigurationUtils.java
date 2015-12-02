package io.firstwave.allium.api.utils;

import io.firstwave.allium.api.Configurable;

/**
 * Created by obartley on 12/1/15.
 */
public class ConfigurationUtils {

    private ConfigurationUtils() {} // not intended for instantiation

    public static boolean getOpt(Configurable configurable, String key) {
        return configurable.getConfiguration().getOption(key);
    }

    public static int getOptSet(Configurable configurable, String key) {
        return configurable.getConfiguration().getOptionSetIndex(key);
    }

    public static int getInt(Configurable configurable, String key) {
        return configurable.getConfiguration().getInteger(key);
    }

    public static float getFloat(Configurable configurable, String key) {
        return configurable.getConfiguration().getFloat(key);
    }

    public static String getString(Configurable configurable, String key) {
        return configurable.getConfiguration().getString(key);
    }
}
