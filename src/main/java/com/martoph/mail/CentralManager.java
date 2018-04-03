package com.martoph.mail;

import com.martoph.mail.listener.InventoryListener;
import com.martoph.mail.mail.MailItem;
import com.martoph.mail.mail.MailManager;
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

        initFolder();
        initMail();
        initLang();
    }

    private void initFolder() {
        File folder = new File(plugin.getDataFolder() + "/");

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private void initMail() {
        File mailFile = new File(getPlugin().getDataFolder(), "mail.json");

        try {
            boolean created = mailFile.createNewFile();
            if (created)
                MartophsMail.sendMessage("Created mail file.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        readMail();
    }

    public void saveMail() {

        HashMap<UUID, List<String>> map = new HashMap<>();

        mailManager.getMessageMap().asMap().forEach((key, value) -> {
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
                UUID uuid = UUID.fromString(key);

                for (String string : (List<String>) jsonObject.get(key)) {
                    MailItem mailItem = new MailItem(string, UUID.fromString(key));
                    getMailManager().getMessageMap().get(uuid).add(mailItem);
                }

            }

        } catch (ParseException ignored) {
        }

    }

    private void initLang() {
        File lang = new File(getPlugin().getDataFolder(), "en_US.lang");

        try {
            boolean created = lang.createNewFile();
            if (created)
                MartophsMail.sendMessage("Created lang file.");
        } catch (IOException e) {
            e.printStackTrace();
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
