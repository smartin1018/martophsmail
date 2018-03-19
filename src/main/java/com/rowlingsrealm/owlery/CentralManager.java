package com.rowlingsrealm.owlery;

import com.rowlingsrealm.owlery.listener.InventoryListener;
import com.rowlingsrealm.owlery.mail.MailItem;
import com.rowlingsrealm.owlery.mail.MailManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class CentralManager extends SimpleListener {

    private Plugin plugin;
    private MailManager mailManager;
    private InventoryListener inventoryListener;
    private File mailFile;

    CentralManager(JavaPlugin plugin) {
        super(plugin, "Central Manager");

        mailManager = new MailManager(plugin);
        inventoryListener = new InventoryListener(plugin);

        this.plugin = plugin;
        mailFile = new File(getPlugin().getDataFolder(), "mail.json");

        initMail();
        initLang();
    }

    private void initMail() {
        File mailFile = new File(getPlugin().getDataFolder(), "mail.json");

        if (!mailFile.exists()) {
            plugin.saveResource("mail.json", false);
        }

        readMail();
    }

    public void saveMail() {

        HashMap<UUID, List<String>> map = new HashMap<>();

        mailManager.getMessageMap().forEach((key, value) -> {
            List<String> strings = new ArrayList<>();

            value.forEach(mailItem -> strings.add(mailItem.getJSONString()));

            if (strings.isEmpty())
                return;

            map.put(key, strings);

        });

        JSONObject jsonObject = new JSONObject(map);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mailFile))) {

            bufferedWriter.write(jsonObject.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private void readMail() {

        StringBuilder json = new StringBuilder();

        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(mailFile), Charset.forName("Cp1252")))) {

            String line;
            while ((line = input.readLine()) != null) {
                json.append(line);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json.toString());

            if (jsonObject == null)
                return;

            Iterator<String> keysItr = (Iterator<String>) jsonObject.keySet().iterator();
            while (keysItr.hasNext()) {

                String key = keysItr.next();
                List<MailItem> mailItems = new ArrayList<>();

                for (String string : (List<String>) jsonObject.get(key)) {
                    MailItem mailItem = new MailItem(string, UUID.fromString(key));
                    mailItems.add(mailItem);
                }

                getMailManager().getMessageMap().put(UUID.fromString(key), mailItems);
            }

        } catch (ParseException ignored) {
        }

    }

    private void initLang() {
        File lang = new File(getPlugin().getDataFolder(), "en_US.lang");

        if (!lang.exists()) {
            plugin.saveResource("en_US.lang", false);
        }

        Properties properties = new Properties();

        try (InputStreamReader input = new InputStreamReader(new FileInputStream(lang), Charset.forName("Cp1252"))) {

            properties.load(input);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lang, true))) {

                int i = 0;
                for (Map.Entry<String, String> entry : Lang.getDefaults().entrySet()) {
                    if (!properties.containsKey(entry.getKey())) {
                        bufferedWriter.write((i > 0 ? "\n" : "") + entry.getKey() + "=" + entry.getValue());
                    }
                    i++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            properties.load(input);

            for (Object o : properties.keySet())
                Lang.setProperty((String) o, properties.getProperty((String) o));

        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public InventoryListener getInventoryListener() {
        return inventoryListener;
    }
}
