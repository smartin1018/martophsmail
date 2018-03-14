package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.Lang;
import com.rowlingsrealm.owlery.Owlery;
import com.rowlingsrealm.owlery.anvil.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MailItem {

    private boolean read;
    private long timeSentMillis;
    private UUID sender;
    private UUID receiever;
    private String message;
    private List<ItemStack> itemStacks = new ArrayList<>();

    public MailItem(UUID sender) {
        Player player = Bukkit.getPlayer(sender);

        AnvilGUI gui = new AnvilGUI(player, event -> {
            if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                event.setWillClose(true);
                event.setWillDestroy(true);
                String name = event.getName();
                player.sendMessage(Lang.getProperty("enter-message", "{PLAYER}", name));

                setReceiever(UUID.fromString(name));

                new MailCreator(this);
            } else {
                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });

        try {
            gui.open();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public MailItem(UUID receiver, UUID sender, String message, List<ItemStack> itemStacks) {
        read = false;
        timeSentMillis = System.currentTimeMillis();
        this.sender = sender;
        this.message = message;
        this.itemStacks = itemStacks;

        Owlery.getCentralManager().getMailManager().addMessage(receiver, this);
    }

    void setReceiever(UUID receiever) {
        this.receiever = receiever;
    }

    public UUID getReceiever() {
        return receiever;
    }

    public boolean wasRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getTimeSentMillis() {
        return timeSentMillis;
    }

    public UUID getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }
}
