package com.rowlingsrealm.owlery;

import java.util.HashMap;
import java.util.Map;

public class Lang {

    private static Map<String, String> properties = new HashMap<>();

    public static Map<String, String> getProperties() {
        return properties;
    }

    public static void setProperty(String key, String property) {
        properties.put(key, property.replace("&", "§"));
    }

    public static Map<String, String> getDefaults() {
        properties.put("usage",  C.DRed + "Usage: /{COMMAND} {PARAMS}");
        properties.put("no-message-pending", C.DRed + "No message to end!");
        properties.put("enter-message", C.Green + "Sending message to: {PLAYER}. Enter message in chat. You may use multiple messages. Use /endmessage to continue.");

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
