package com.martoph.mail.listener;

import com.martoph.mail.C;
import com.martoph.mail.Lang;
import com.martoph.mail.MartophsMail;
import com.martoph.mail.SimpleListener;
import com.martoph.mail.mail.MailCreator;
import com.martoph.mail.mail.MailItem;
import com.martoph.mail.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InventoryListener extends SimpleListener {

    private HashMap<UUID, Integer> inboxViewers = new HashMap<>();
    private List<UUID> mailViewers = new ArrayList<>();
    private List<UUID> itemSenders = new ArrayList<>();
    private List<MailItem> sureToDelete = new ArrayList<>();

    public InventoryListener(JavaPlugin plugin) {
        super(plugin, "Inventory Listener");
    }

    public void addMailViewer(Player player, int page) {
        inboxViewers.put(player.getUniqueId(), page);
    }

    public List<UUID> getItemSenders() {
        return itemSenders;
    }

    public List<UUID> getMailViewers() {
        return mailViewers;
    }

    public HashMap<UUID, Integer> getInboxViewers() {
        return inboxViewers;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        Set<Integer> slots = event.getRawSlots();

        for (Integer slot : slots) {
            if (inboxViewers.containsKey(event.getWhoClicked().getUniqueId())) {
                if (slot < event.getView().getTopInventory().getSize())
                    event.setCancelled(true);
            }

            if (mailViewers.contains(event.getWhoClicked().getUniqueId())) {

                if (!itemSenders.contains(event.getWhoClicked().getUniqueId())) {
                    if (slot < event.getView().getTopInventory().getSize())
                        event.setCancelled(true);
                }

                if (slot >= event.getView().getTopInventory().getSize())
                    continue;

                if (slot <= 9 || slot >= event.getView().getTopInventory().getSize() - 9)
                    event.setCancelled(true);

                if (slot % 9 == 0 || slot % 9 == 8)
                    event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null)
            return;

        if (inboxViewers.containsKey(player.getUniqueId())) {

            if (event.getRawSlot() < event.getView().getTopInventory().getSize())
                event.setCancelled(true);

            if (clicked.getType() == Material.BARRIER)
                player.closeInventory();

            if (clicked.getType() == Material.EMERALD) {
                new MailItem(player.getUniqueId());
            }

            if (clicked.getType() == Material.BOOK) {
                List<String> lore = clicked.getItemMeta().getLore();

                // Hex
                MailItem mailItem = new MailItem().parse(C.stripColor(lore.get(3)));

                if (event.getClick() == ClickType.RIGHT) {

                    if (sureToDelete.contains(mailItem)) {
                        MartophsMail.getCentralManager().getMailManager().getMessageMap().get(player.getUniqueId()).remove(mailItem);
                        event.getClickedInventory().remove(clicked);
                        player.sendMessage(Lang.getProperty("deleted"));
                        return;
                    }

                    player.sendMessage(Lang.getProperty("sure-to-delete"));
                    sureToDelete.add(mailItem);
                    return;
                }

                mailItem.open(player);
                mailItem.setRead(true);
                mailViewers.add(player.getUniqueId());
                inboxViewers.remove(player.getUniqueId());
            }

            if (clicked.getType() == Material.SIGN) {
                if (clicked.getItemMeta().getDisplayName().equals(C.Green + "Previous Page")) {
                    MartophsMail.getCentralManager().getMailManager().openOwlery(player, inboxViewers.get(player.getUniqueId()) - 1);
                    MartophsMail.sendMessage(inboxViewers.get(player.getUniqueId()));
                }

                if (clicked.getItemMeta().getDisplayName().equals(C.Green + "Next Page")) {
                    MartophsMail.getCentralManager().getMailManager().openOwlery(player, inboxViewers.get(player.getUniqueId()) + 1);
                    MartophsMail.sendMessage(inboxViewers.get(player.getUniqueId()));
                }
            }
        }

        if (itemSenders.contains(player.getUniqueId())) {
            if (clicked.isSimilar(UtilInv.createItem(Material.EMERALD, C.Green + "Send Mail", new String[]{C.Gray + "Click me to send mail!"}, 1))) {
                Inventory inventory = UtilInv.surroundInventory(event.getClickedInventory(), new ItemStack(Material.AIR));
                ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
                items.removeIf(Objects::isNull);
                MailCreator mailCreator = new MailCreator().parse(player.getUniqueId());

                new MailItem(mailCreator.getSender(), mailCreator.getReceiver(), mailCreator.getMessageStreamline(), items);

                itemSenders.remove(player.getUniqueId());
                MartophsMail.getCentralManager().getMailManager().getCreators().remove(mailCreator);
                Bukkit.getScheduler().runTaskLater(getPlugin(), player::closeInventory, 1);
                player.sendMessage(Lang.getProperty("mail-sent"));
                Bukkit.getPlayer(mailCreator.getReceiver()).sendMessage(Lang.getProperty("mail-received", "{PLAYER}", player.getName()));
            }
        }

        if (mailViewers.contains(player.getUniqueId())) {
            int slot = event.getRawSlot();


            if (!itemSenders.contains(player.getUniqueId())) {

                InventoryAction action = event.getAction();

                boolean playerInv = slot >= event.getView().getTopInventory().getSize();
                boolean cancelled = true;

                switch (action) {
                    case HOTBAR_SWAP:
                        if (!playerInv) cancelled = false;
                        break;
                    case MOVE_TO_OTHER_INVENTORY:
                        if (!playerInv) cancelled = false;
                        break;
                    case COLLECT_TO_CURSOR:
                        if (!playerInv) cancelled = false;
                        break;
                    case PICKUP_ALL:
                        cancelled = false;
                        break;
                    case PICKUP_HALF:
                        cancelled = false;
                        break;
                    case PICKUP_ONE:
                        cancelled = false;
                        break;
                    case PICKUP_SOME:
                        cancelled = false;
                        break;
                    case PLACE_ALL:
                        if (playerInv) cancelled = false;
                        break;
                    case PLACE_ONE:
                        if (playerInv) cancelled = false;
                        break;
                    case PLACE_SOME:
                        if (playerInv) cancelled = false;
                        break;
                    case SWAP_WITH_CURSOR:
                        if (playerInv) cancelled = false;
                        break;
                }

                event.setCancelled(cancelled);
            }

            if (slot >= event.getView().getTopInventory().getSize())
                return;

            if (slot <= 9 || slot >= event.getView().getTopInventory().getSize() - 9)
                event.setCancelled(true);

            if (slot % 9 == 0 || slot % 9 == 8)
                event.setCancelled(true);
        }

        if (clicked.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) clicked.getItemMeta();
            MailItem mailItem = new MailItem().parse(bookMeta.getAuthor());

            if (mailItem != null && !mailItem.getMessage().isEmpty()) {

                if (event.getClickedInventory().getType() == InventoryType.CHEST) {
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(clicked);

                    if (!map.isEmpty()) {
                        player.sendMessage(Lang.getProperty("full-inv"));
                    }

                    event.setCancelled(true);
                } else {
                    player.getInventory().remove(clicked);
                    player.updateInventory();
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        inboxViewers.remove(player.getUniqueId());

        if (itemSenders.contains(player.getUniqueId())) {

            inventory = UtilInv.surroundInventory(inventory, new ItemStack(Material.AIR));
            ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
            items.removeIf(Objects::isNull);
            items.forEach(itemStack -> UtilInv.attemptAddToInv(itemStack, player));

            MartophsMail.getCentralManager().getMailManager().getCreators().remove(new MailCreator().parse(player.getUniqueId()));
            itemSenders.remove(player.getUniqueId());
            player.sendMessage(Lang.getProperty("cancelled-delivery"));
        }

        if (mailViewers.contains(player.getUniqueId())) {
            mailViewers.remove(player.getUniqueId());
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
        }
    }
}
