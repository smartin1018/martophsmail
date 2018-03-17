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

        return new HashMap<String, String>() {{
            put("usage", C.DRed + "Usage: /{COMMAND} {PARAMS}");
            put("no-message-pending", C.DRed + "No message to end!");
            put("enter-message", C.Green + "Sending message to: {PLAYER}. Enter message in chat. You may use multiple messages. Use /endmessage to continue.");
            put("add-items-inv", C.Green + "Send items with your message");
            put("mail-menu-inv", C.Green + "Mail");
            put("cancelled-delivery", C.DRed + "Cancelled delivery.");
            put("message-entered", C.Green + "Added message: " + C.Yellow + "{MESSAGE}");
            put("mail-sent", C.Green + "Mail sent!");
            put("date-format", "yyyy-MM-dd' @ 'HH:mm:ss");
            put("long-message", C.DRed + "Message too long!");
            put("full-inv", C.DRed + "Full inventory!");
            put("invalid-player", C.DRed + "Player is not online or doesn't exist.");
            put("sure-to-delete", C.DRed + "Are you sure you want to delete? R-Click again. L-Click to read.");
            put("deleted", C.DRed + "Deleted.");
            put("no-message-to-end", C.DRed + "No message to end!");
            put("mail-received", C.Green + "Mail received from {PLAYER}!");
        }};
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
