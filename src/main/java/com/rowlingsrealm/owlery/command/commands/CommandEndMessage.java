package com.rowlingsrealm.owlery.command.commands;

import com.rowlingsrealm.owlery.C;
import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.command.SimpleCommand;
import com.rowlingsrealm.owlery.mail.MailCreator;
import com.rowlingsrealm.owlery.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        if (mailCreator.getMessageStreamline().length() > 256 * 50) {
            player.sendMessage(Lang.getProperty("long-message"));
            return true;
        }

        Inventory inventory = UtilInv.surroundInventory(Bukkit.createInventory(null, 54, Lang.getProperty("add-items-inv")), new ItemStack(Material.STAINED_GLASS_PANE));
        inventory.setItem(4, UtilInv.addGlow(new ItemStack(Material.BOOK)));
        inventory.setItem(49, UtilInv.createItem(Material.EMERALD, C.Green + "Send Mail", new String[] {C.Gray + "Click me to send mail!"},  1));

        Owlery.getCentralManager().getInventoryListener().getMailViewers().add(player);

        // Owlery.getCentralManager().getMailManager().getCreators().remove(mailCreator);
        return true;

    }
}
