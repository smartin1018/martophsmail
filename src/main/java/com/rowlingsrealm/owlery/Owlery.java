package com.rowlingsrealm.owlery;

import com.rowlingsrealm.owlery.command.CommandEndMessage;
import com.rowlingsrealm.owlery.mail.MailCreator;
import com.rowlingsrealm.owlery.mail.MailItem;
import com.rowlingsrealm.owlery.npc.TraitOwlery;
import com.rowlingsrealm.owlery.util.UtilInv;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Owlery extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Server server;
    private static CentralManager centralManager;

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

    public void onEnable() {

        plugin = this;
        server = getServer();
        centralManager = new CentralManager(plugin);

        PluginManager pluginManager = server.getPluginManager();
        Plugin citizensPlugin = pluginManager.getPlugin("Citizens");
        if (citizensPlugin == null || !citizensPlugin.isEnabled()) {
            sendMessage(C.DRed + "CITIZENS IS NOT ENABLED - DISABLING");
            pluginManager.disablePlugin(this);
        }

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitOwlery.class).withName("Owlery"));

        getCommand("endmessage").setExecutor(new CommandEndMessage());

    }


    @SuppressWarnings("Duplicates")
    public void onDisable() {

        getCentralManager().getInventoryListener().getItemSenders().forEach(player -> {

            Inventory inventory = player.getOpenInventory().getTopInventory();

            inventory = UtilInv.surroundInventory(inventory, new ItemStack(Material.AIR));
            ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
            items.removeIf(Objects::isNull);
            items.forEach(itemStack -> UtilInv.attemptAddToInv(itemStack, player));

            Owlery.getCentralManager().getMailManager().getCreators().remove(new MailCreator().parse(player.getUniqueId()));
            player.sendMessage(Lang.getProperty("cancelled-delivery"));

            player.closeInventory();
        });

        getCentralManager().getInventoryListener().getMailViewers().forEach(player -> {

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

        getCentralManager().getInventoryListener().getInboxViewers().keySet().forEach(Player::closeInventory);

        getCentralManager().saveMail();
    }
}
