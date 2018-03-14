package com.rowlingsrealm.owlery;

import com.rowlingsrealm.owlery.npc.TraitOwelry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Owlery extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Server server;
    private static CentralManager centralManager;

    public void onEnable() {

        plugin = this;
        server = getServer();

        PluginManager pluginManager = server.getPluginManager();
        Plugin citizensPlugin = pluginManager.getPlugin("Citizens");
        if (citizensPlugin == null || !citizensPlugin.isEnabled()) {
            sendMessage(C.DRed + "CITIZENS IS NOT ENABLED - DISABLING");
            pluginManager.disablePlugin(this);
        }

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitOwelry.class));

        centralManager = new CentralManager(plugin);
    }

    public static void sendMessage(Object message) {
        ConsoleCommandSender console = server.getConsoleSender();

        console.sendMessage(message + "");
    }

    public static CentralManager getCentralManager() {
        return centralManager;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
