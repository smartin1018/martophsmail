package com.martoph.mail.mail;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.martoph.mail.C;
import com.martoph.mail.Lang;
import com.martoph.mail.MartophsMail;
import com.martoph.mail.SimpleListener;
import com.martoph.mail.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailManager extends SimpleListener {

    private Multimap<UUID, MailItem> messages = ArrayListMultimap.create();
    private ArrayList<MailCreator> creators = new ArrayList<>();


    public MailManager(JavaPlugin plugin) {
        super(plugin, "Mail Manager");
    }

    public Multimap<UUID, MailItem> getMessageMap() {
        return messages;
    }

    public void addMessage(UUID uuid, MailItem mailItem) {
        messages.get(uuid).add(mailItem);
    }

    public void openOwlery(Player player, int page) {

        Inventory inventory = UtilInv.surroundInventory(Bukkit.createInventory(null, 27, Lang.getProperty("mail-menu-inv")), new ItemStack(Material.STAINED_GLASS_PANE));
        inventory.setItem(19, UtilInv.createItem(Material.EMERALD, C.Green + "Compose", new String[]{C.Gray + "Click me to compose a message."}, 1));

        List<MailItem> messages = new ArrayList<>(this.messages.get(player.getUniqueId()));

        for (int i = 1; i < 8; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        if (messages.isEmpty()) {
            player.openInventory(inventory);
            MartophsMail.getCentralManager().getInventoryListener().addMailViewer(player, 0);
            return;
        }

        for (int i = page * 14; i < 14 * (page + 1); i++) {
            try {
                inventory.addItem(messages.get(i).toItemStack());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        if (page > 0) {
            inventory.setItem(25, UtilInv.createItem(Material.SIGN, C.Green + "Previous Page", new String[]{}, 1));
        }

        if (messages.size() > 14 * (page + 1)) {
            inventory.setItem(26, UtilInv.createItem(Material.SIGN, C.Green + "Next Page", new String[]{C.Gray + "You have " + C.Red + (messages.size() - 14 * page) + " more messages."}, 1));
        }

        player.openInventory(inventory);
        MartophsMail.getCentralManager().getInventoryListener().addMailViewer(player, page);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        MailCreator mailCreator = new MailCreator().parse(uuid);

        if (mailCreator == null)
            return;

        String message = event.getMessage();
        mailCreator.addMessage(message);

        player.sendMessage(Lang.getProperty("message-entered", "{MESSAGE}", message));
        event.setCancelled(true);
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        MailCreator mailCreator = new MailCreator().parse(uuid);

        if (mailCreator == null)
            return;

        creators.remove(mailCreator);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) item.getItemMeta();
            MailItem mailItem = new MailItem().parse(bookMeta.getAuthor());

            if (mailItem != null)
                event.setCancelled(true);
        }
    }

    public ArrayList<MailCreator> getCreators() {
        return creators;
    }

}
