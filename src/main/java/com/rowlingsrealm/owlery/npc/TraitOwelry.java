package com.rowlingsrealm.owlery.npc;

import com.rowlingsrealm.owlery.Owlery;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class TraitOwelry extends Trait {

    public TraitOwelry() {
        super("Owlery");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {

        if (!event.getNPC().hasTrait(this.getClass()))
            return;

        Owlery.getCentralManager().getMailManager().openOwlery(event.getClicker(), 0);
    }

    @Override
    public void onAttach() {
        Owlery.sendMessage(npc.getName() + " was assigned as an Owlery");
    }
}
