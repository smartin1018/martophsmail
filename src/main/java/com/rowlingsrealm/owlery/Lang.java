package com.rowlingsrealm.owlery;

import java.util.HashMap;

public class Lang {

    private static HashMap<String, String> properties = new HashMap<>();

    public static HashMap<String, String> getProperties() {
        return properties;
    }

    public static void setProperty(String key, String property) {
        properties.put(key, property.replace("&", "§"));
    }

    public static HashMap<String, String> getDefaults() {
        properties.put("usage",  C.DRed + "Usage: /{COMMAND} {PARAMS}");

        return properties;
    }

    public static String getProperty(String key, String... replacements) {
        if (replacements.length % 2 != 0)
            return null;

        String value = properties.get(key);
        for (int i = 0; i < replacements.length; i+= 2) {
            value.replace(replacements[i], replacements[i + 1]);
        }

        return value;
    }
}
