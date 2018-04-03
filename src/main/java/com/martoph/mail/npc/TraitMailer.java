package com.martoph.mail.npc;

import com.martoph.mail.MartophsMail;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class TraitMailer extends Trait {

    public TraitMailer() {
        super("Mailer");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {

        if (!event.getNPC().hasTrait(this.getClass()))
            return;

        MartophsMail.getCentralManager().getMailManager().openOwlery(event.getClicker(), 0);
    }

    @Override
    public void onAttach() {
        MartophsMail.sendMessage(npc.getName() + " was assigned as a Mail NPC");
    }
}
