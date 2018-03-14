package com.rowlingsrealm.owlery;

import com.rowlingsrealm.owlery.command.CommandManager;
import com.rowlingsrealm.owlery.mail.MailManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

public class CentralManager extends SimpleListener {

    private Plugin plugin;
    private MailManager mailManager;
    private CommandManager commandManager;

    public CentralManager(JavaPlugin plugin) {
        super(plugin, "Central Manager");

        mailManager = new MailManager(plugin);
        commandManager = new CommandManager(plugin);

        this.plugin = plugin;

        initMail();
        initLang();
    }

    public void initMail() {
        File mailFile = new File(plugin.getDataFolder(), "mail.yml");

        if (!mailFile.exists()) {
            plugin.saveResource("mail.yml", false);
        }
    }

    public void initLang() {
        File lang = new File(plugin.getDataFolder(), "en_US.lang");

        if (!lang.exists()) {
            plugin.saveResource("en_US.lang", false);
        }

        Properties properties = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(lang);

            for (Map.Entry<String, String> entry : Lang.getDefaults().entrySet()) {
                if (!properties.containsKey(entry.getKey())) {
                    properties.setProperty(entry.getKey(), entry.getValue());
                }
            }

            for (Object o : properties.keySet()) {
                Lang.setProperty((String) o, properties.getProperty((String) o));
            }

            properties.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
