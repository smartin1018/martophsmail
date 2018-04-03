package com.martoph.mail.command;

import com.martoph.mail.C;
import com.martoph.mail.Lang;
import com.martoph.mail.MartophsMail;
import com.martoph.mail.mail.MailCreator;
import com.martoph.mail.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandEndMessage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Not Supported!");
            return true;
        }

        Player player = (Player) commandSender;

        MailCreator mailCreator = new MailCreator().parse(player.getUniqueId());

        if (mailCreator == null) {
            player.sendMessage(Lang.getProperty("no-message-to-end"));
            return false;
        }

        if (mailCreator.getMessageStreamline().length() > 256 * 50) {
            player.sendMessage(Lang.getProperty("long-message"));
            return true;
        }

        Inventory inventory = UtilInv.surroundInventory(Bukkit.createInventory(null, 54, Lang.getProperty("add-items-inv")), new ItemStack(Material.STAINED_GLASS_PANE));
        inventory.setItem(4, UtilInv.addGlow(new ItemStack(Material.BOOK)));
        inventory.setItem(49, UtilInv.createItem(Material.EMERALD, C.Green + "Send Mail", new String[]{C.Gray + "Click me to send mail!"}, 1));

        player.openInventory(inventory);

        MartophsMail.getCentralManager().getInventoryListener().getItemSenders().add(player.getUniqueId());
        MartophsMail.getCentralManager().getInventoryListener().getMailViewers().add(player.getUniqueId());
        return true;
    }
}
