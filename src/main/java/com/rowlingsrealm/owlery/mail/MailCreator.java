package com.rowlingsrealm.owlery.mail;

import com.rowlingsrealm.owlery.Owlery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailCreator {

    private boolean done = false;
    private UUID sender;
    private UUID receiver;
    private List<String> messages = new ArrayList<>();

    public MailCreator() {
    }

    public MailCreator(MailItem mailItem) {
        this(mailItem.getSender(), mailItem.getReceiever());
    }

    public MailCreator(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
        Owlery.getCentralManager().getMailManager().getCreators().add(this);
    }

    public MailCreator parse(UUID uuid) {
        for (MailCreator creator : Owlery.getCentralManager().getMailManager().getCreators()) {
            if (creator.getSender() == uuid)
                return creator;
        }

        return null;
    }

    public boolean isDone() {
        return done;
    }

    public UUID getSender() {
        return sender;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public String getMessageStreamline() {
        StringBuilder stringBuilder = new StringBuilder();
        messages.forEach(s -> stringBuilder.append(s).append(" "));

        return stringBuilder.toString();
    }
}