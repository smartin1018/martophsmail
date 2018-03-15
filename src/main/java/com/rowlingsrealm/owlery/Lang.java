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
        properties.put("add-items-inv", C.Green + "Send items with your message");
        properties.put("mail-menu-inv", C.Green + "Mail");
        properties.put("cancelled-delivery", C.DRed + "Cancelled delivery.");
        properties.put("message-entered", C.Green + "Added message: " + C.Yellow + "{MESSAGE}");
        properties.put("mail-sent", C.Green + "Mail sent!");
        properties.put("date-format", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        properties.put("long-message", C.DRed + "Message too long!");
        properties.put("full-inv", C.DRed + "Full inventory!");

        return properties;
    }

    public static String getProperty(String key, String... replacements) {
        if (replacements.length % 2 != 0)
            return null;

        String value = properties.get(key);
        for (int i = 0; i < replacements.length; i+= 2) {
            value = value.replace(replacements[i], replacements[i + 1]);
        }

        return value;
    }
}
