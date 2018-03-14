package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.SimpleListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MailManager extends SimpleListener {

    private HashMap<UUID, List<MailItem>> messages = new HashMap<>();

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
}
