package com.martoph.mail.mail;

import com.martoph.mail.MartophsMail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailCreator {

    private UUID sender;
    private UUID receiver;
    private List<String> messages = new ArrayList<>();

    public MailCreator() {
    }

    MailCreator(MailItem mailItem) {
        this(mailItem.getSender(), mailItem.getReceiver());
    }

    private MailCreator(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
        MartophsMail.getCentralManager().getMailManager().getCreators().add(this);
    }

    public MailCreator parse(UUID uuid) {
        for (MailCreator creator : MartophsMail.getCentralManager().getMailManager().getCreators()) {
            if (creator.getSender().equals(uuid))
                return creator;
        }

        return null;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
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