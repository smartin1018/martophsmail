package com.rowlingsrealm.owlery.mail;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailItem {

    private boolean read;
    private long timeSentMillis;
    private UUID sender;
    private String message;
    private List<ItemStack> itemStacks = new ArrayList<>();

    public MailItem(UUID sender, String message, List<ItemStack> itemStacks) {
        read = false;
        timeSentMillis = System.currentTimeMillis();
        this.sender = sender;
        this.message = message;
        this.itemStacks = itemStacks;
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
