package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.SimpleListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

    public void addMessage(UUID uuid, MailItem mailItem) {
        messages.computeIfAbsent(uuid, k -> new ArrayList<>());

        messages.get(uuid).add(mailItem);
    }

    public void removeMessage(UUID uuid, MailItem mailItem) {
        messages.get(uuid).remove(mailItem);
    }

    public void openOwelry(Player player) {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        MailCreator mailCreator = new MailCreator().parse(uuid);

        if (mailCreator == null)
            return;

        String message = event.getMessage();
        mailCreator.addMessage(message);
    }

    public ArrayList<MailCreator> getCreators() {
        return creators;
    }

}
