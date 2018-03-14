package com.rowlingsrealm.owlery.command;

import com.rowlingsrealm.owlery.SimpleListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class CommandManager extends SimpleListener {

    private CommandMap commandMap;

    public CommandManager(JavaPlugin plugin) {
        super(plugin, "Command Manager");

        final Field bukkitCommandMap;
        try {
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
             commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }
}
