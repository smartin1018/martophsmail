package com.rowlingsrealm.owlery.command.commands;

import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.command.SimpleCommand;
import com.rowlingsrealm.owlery.mail.MailCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandEndMessage extends SimpleCommand {

    public CommandEndMessage() {
        super("endmessage", "Ends an Owlery message", new ArrayList<String>() {{
            add("endm");
        }});

        setUsage(Lang.getProperty("no-message-pending"));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Not Supported!");
            return true;
        }

        Player player = (Player) commandSender;

        MailCreator mailCreator = new MailCreator().parse(player.getUniqueId());

        if (mailCreator == null)
            return false;

        

        Owlery.getCentralManager().getMailManager().getCreators().remove(mailCreator);
        return true;

    }
}
