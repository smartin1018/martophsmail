package com.rowlingsrealm.owlery.command;

import com.rowlingsrealm.owlery.C;
import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owelry;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SimpleCommand extends BukkitCommand {

    public SimpleCommand(String name, String description, List<String> aliases) {
        super(name, description, "", aliases);

        setUsage(getUsage());
        Owelry.getCentralManager().getCommandManager().getCommandMap().register(name, this);
    }

    public String getUsage(String... parameters) {

        String formattedParams;
        StringBuilder stringBuilder = new StringBuilder();

        for (String parameter : parameters) {
            stringBuilder.append('<').append(parameter).append("> ");
        }

        formattedParams = stringBuilder.toString();

        return Lang.getProperty("usage","{COMMAND}", getName(), "{PARAMETERS}", formattedParams);
    }
}
