package com.rowlingsrealm.owlery.command.commands;

import com.rowlingsrealm.owlery.command.SimpleCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandEndMessage extends SimpleCommand {

    public CommandEndMessage() {
        super("endmessage", "Ends an Owelry message", new ArrayList<String>() {{
            add("endm");
        }});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
