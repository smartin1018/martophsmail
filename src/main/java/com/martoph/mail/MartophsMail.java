package com.martoph.mail;

import com.martoph.mail.command.CommandEndMessage;
import com.martoph.mail.command.CommandMail;
import com.martoph.mail.mail.MailCreator;
import com.martoph.mail.mail.MailItem;
import com.martoph.mail.npc.TraitMailer;
import com.martoph.mail.util.UtilInv;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MartophsMail extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Server server;
    private static CentralManager centralManager;

    private static Permission mailSelfPermission = new Permission("mmail.sendtoself");
    private static Permission mailPermission = new Permission("mmail.mail");

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

    public static Permission getMailSelfPermission() {
        return mailSelfPermission;
    }


    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    public static Permission getMailPermission() {
        return mailPermission;
    }

    public void onEnable() {

        plugin = this;
        server = getServer();
        centralManager = new CentralManager(plugin);

        PluginManager pluginManager = server.getPluginManager();
        Plugin citizensPlugin = pluginManager.getPlugin("Citizens");
        if (citizensPlugin != null && citizensPlugin.isEnabled()) {
            sendMessage("Citizens support enabled!");
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitMailer.class).withName("Mailer"));
        }


        getCommand("endmessage").setExecutor(new CommandEndMessage());
        getCommand("mmail").setExecutor(new CommandMail());

        pluginManager.addPermission(new Permission("mmail.sendtoself"));
        pluginManager.addPermission(new Permission("mmail.mail"));

    }

    @SuppressWarnings("Duplicates")
    public void onDisable() {

        getCentralManager().getInventoryListener().getItemSenders().forEach(uuid -> {

            Player player = Bukkit.getPlayer(uuid);

            Inventory inventory = player.getOpenInventory().getTopInventory();

            inventory = UtilInv.surroundInventory(inventory, new ItemStack(Material.AIR));
            ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
            items.removeIf(Objects::isNull);
            items.forEach(itemStack -> UtilInv.attemptAddToInv(itemStack, player));

            MartophsMail.getCentralManager().getMailManager().getCreators().remove(new MailCreator().parse(player.getUniqueId()));
            player.sendMessage(Lang.getProperty("cancelled-delivery"));

            player.closeInventory();
        });

        getCentralManager().getInventoryListener().getMailViewers().forEach(uuid -> {

            Player player = Bukkit.getPlayer(uuid);

            Inventory inventory = player.getOpenInventory().getTopInventory();
            ItemStack book = inventory.getItem(4);

            if (book == null)
                return;

            if (book.getType() == Material.WRITTEN_BOOK) {

                BookMeta bookMeta = (BookMeta) book.getItemMeta();

                MailItem mailItem = new MailItem().parse(bookMeta.getAuthor());

                if (mailItem != null) {
                    inventory = UtilInv.surroundInventory(inventory, new ItemStack(Material.AIR));
                    ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
                    items.removeIf(Objects::isNull);

                    mailItem.setItemStacks(items);

                }
            }

            player.closeInventory();

        });

        getCentralManager().getInventoryListener().getInboxViewers().keySet().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.closeInventory();
        });

        getCentralManager().saveMail();

        server.getPluginManager().removePermission(mailPermission);
        server.getPluginManager().removePermission(mailSelfPermission);
    }
}
