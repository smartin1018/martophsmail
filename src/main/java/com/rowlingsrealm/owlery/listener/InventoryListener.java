package com.rowlingsrealm.owlery.listener;

import com.rowlingsrealm.owlery.C;
import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.SimpleListener;
import com.rowlingsrealm.owlery.mail.MailCreator;
import com.rowlingsrealm.owlery.mail.MailItem;
import com.rowlingsrealm.owlery.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InventoryListener extends SimpleListener {

    private HashMap<Player, Integer> inboxViewers = new HashMap<>();
    private List<Player> mailViewers = new ArrayList<>();
    private List<Player> itemSenders = new ArrayList<>();

    public InventoryListener(JavaPlugin plugin) {
        super(plugin, "Inventory Listener");
    }

    public HashMap<Player, Integer> getInboxViewers() {
        return inboxViewers;
    }

    public void addMailViewer(Player player, int page) {
        inboxViewers.put(player, page);
    }

    public List<Player> getItemSenders() {
        return itemSenders;
    }

    public void addItemSender(Player player) {
        itemSenders.add(player);
    }

    public List<Player> getMailViewers() {
        return mailViewers;
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!inboxViewers.containsKey(player))
            return;

        // Cancel click if player is clicking top inventory
        List<Integer> slotsInvolved = new ArrayList<>();

        if (event instanceof InventoryClickEvent) {
            slotsInvolved.add(((InventoryClickEvent) event).getRawSlot());
        }

        if (event instanceof InventoryDragEvent) {
            slotsInvolved.addAll(((InventoryDragEvent) event).getRawSlots());
        }

        InventoryView inventoryView = event.getView();

        for (Integer slot : slotsInvolved) {
            if (slot < inventoryView.getTopInventory().getSize())
                event.setCancelled(true);
        }
    }

    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (inboxViewers.containsKey(player)) {
            if (clicked.getType() == Material.BARRIER)
                player.closeInventory();

            if (clicked.getType() == Material.EMERALD) {
                new MailItem(player.getUniqueId());
            }

            if (clicked.getType() == Material.BOOK) {
                List<String> lore = clicked.getItemMeta().getLore();

                // Hex
                MailItem mailItem = new MailItem().parse(C.stripColor(lore.get(3)));
                mailItem.open(player);
                inboxViewers.remove(player);

            }
        }

        if (itemSenders.contains(player)) {
            Inventory inventory = UtilInv.surroundInventory(event.getClickedInventory(), new ItemStack(Material.AIR));
            if (clicked.isSimilar(UtilInv.createItem(Material.EMERALD, C.Green + "Send Mail", new String[] {C.Gray + "Click me to send mail!"},  1))) {
                List<ItemStack> items = Arrays.asList(inventory.getContents());
                items.removeIf(Objects::isNull);
                MailCreator mailCreator = new MailCreator().parse(player.getUniqueId());

                new MailItem(mailCreator.getSender(), mailCreator.getReceiver(), mailCreator.getMessageStreamline(), items);

                itemSenders.remove(player);
                Owlery.getCentralManager().getMailManager().getCreators().remove(mailCreator);
                Bukkit.getScheduler().runTaskLater(getPlugin(), player::closeInventory, 1);
                player.sendMessage(Lang.getProperty("mail-sent"));
            }
        }

        if (mailViewers.contains(player)) {
            int slot = event.getSlot();

            if (slot <= 9 || slot >= event.getInventory().getSize() - 9)
                event.setCancelled(true);

            if (slot % 9 == 0 || slot % 9 == 8)
                event.setCancelled(true);
        }

        if (clicked.getType() == Material.BOOK) {
            BookMeta bookMeta = (BookMeta) clicked.getItemMeta();
            MailItem mailItem = new MailItem().parse(bookMeta.getAuthor());

            if (mailItem != null) {

                if (event.getInventory().getType() == InventoryType.PLAYER || player.getInventory().contains(clicked)) {
                    player.getInventory().remove(clicked);
                    return;
                }

                if (event.getInventory().getType() == InventoryType.CHEST) {
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(clicked);

                    if (!map.isEmpty()) {
                        player.sendMessage(Lang.getProperty("full-inv"));
                    }
                }

            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (inboxViewers.containsKey(player))
            inboxViewers.remove(player);

        if (itemSenders.contains(player)) {
            Owlery.getCentralManager().getMailManager().getCreators().remove(new MailCreator().parse(player.getUniqueId()));
            itemSenders.remove(player);
            player.sendMessage(Lang.getProperty("cancelled-delivery"));
        }

        if (mailViewers.contains(player)) {
            Inventory inventory = event.getInventory();
            ItemStack book  = inventory.getItem(4);
            if (book.getType() == Material.BOOK) {

                BookMeta bookMeta = (BookMeta) book.getItemMeta();

                if (new MailItem().parse(bookMeta.getAuthor()) != null) {
                    inventory = UtilInv.surroundInventory(inventory, new ItemStack(Material.AIR));
                    List<ItemStack> items = Arrays.asList(inventory.getContents());
                    items.removeIf(Objects::isNull);

                }
            }

            mailViewers.remove(player);
        }

        /* Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getOpenInventory().getType() == InventoryType.CRAFTING)
                inboxViewers.remove(player);
            }, 2); */
    }
}
