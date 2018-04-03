package com.martoph.mail;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleListener implements Listener {
    private String name;
    private JavaPlugin plugin;

    public SimpleListener(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        final Listener listener = this;
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }

    protected final JavaPlugin getPlugin() {
        return this.plugin;
    }



}
