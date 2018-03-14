package com.rowlingsrealm.owlery.listener;

import com.rowlingsrealm.owlery.SimpleListener;
import com.rowlingsrealm.owlery.mail.MailItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener extends SimpleListener {

    private static List<Player> mailViewers = new ArrayList<>();
    private static List<Player> naming = new ArrayList<>();

    public InventoryListener(JavaPlugin plugin) {
        super(plugin, "Inventory Listener");
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!mailViewers.contains(player))
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

        if (!(event instanceof InventoryClickEvent))
            return;
    }

    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!mailViewers.contains(player))
            return;

        if (event.getCurrentItem().getType() == Material.BARRIER)
            player.closeInventory();

        if (event.getCurrentItem().getType() == Material.EMERALD) {
            new MailItem(player.getUniqueId());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (mailViewers.contains(player))
            return;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getOpenInventory().getType() == InventoryType.CRAFTING)
                mailViewers.remove(player);
            }, 2);
    }

    public static List<Player> getMailViewers() {
        return mailViewers;
    }

    public static void addMailViewer(Player player) {
        mailViewers.add(player);
    }
}
