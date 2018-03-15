package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.C;
import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.SimpleListener;
import com.rowlingsrealm.owlery.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MailManager extends SimpleListener {

    private HashMap<UUID, List<MailItem>> messages = new HashMap<>();
    private ArrayList<MailCreator> creators = new ArrayList<>();

    public MailManager(JavaPlugin plugin) {
        super(plugin, "Mail Manager");
    }

    public List<MailItem> getMessages(UUID uuid) {
        return messages.get(uuid);
    }

    public HashMap<UUID, List<MailItem>> getMessageMap() {
        return messages;
    }

    public void addMessage(UUID uuid, MailItem mailItem) {
        messages.computeIfAbsent(uuid, k -> new ArrayList<>());

        messages.get(uuid).add(mailItem);
    }

    public void openOwlery(Player player, int page) {

        Inventory inventory = UtilInv.surroundInventory(Bukkit.createInventory(null, 54, Lang.getProperty("mail-menu-inv")), new ItemStack(Material.STAINED_GLASS_PANE));
        inventory.setItem(46, UtilInv.createItem(Material.EMERALD, C.Green + "Compose", new String[]{C.Gray + "Click me to compose a message."}, 1));

        List<MailItem> messages = this.messages.get(player.getUniqueId());

        for (int i = page * 14; i < 14 * (page + 1); i++) {
            try {

                if (i < 7 * (page + 1)) {
                    inventory.setItem(i + 1, messages.get(i).toItemStack());
                    continue;
                }

                inventory.addItem(messages.get(i).toItemStack());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        if (messages.size() > 14 * page) {
            inventory.setItem(52, UtilInv.createItem(Material.SIGN, C.Green + "Next Page", new String[]{C.Gray + "You have " + C.Red + (messages.size() - 14 * page) + " more messages."}, 1));
        }

        Owlery.getCentralManager().getInventoryListener().addMailViewer(player, 0);

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

    public ArrayList<MailCreator> getCreators() {
        return creators;
    }

}
