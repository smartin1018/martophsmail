package com.rowlingsrealm.owlery.npc;

import com.rowlingsrealm.owlery.Owlery;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class TraitOwelry extends Trait {

    public TraitOwelry() {
        super("Owlery");
    }

    @EventHandler
    public void click(NPCClickEvent event) {
        if (event.getNPC() != npc)
            return;

        Owlery.getCentralManager().getMailManager().openOwelry(event.getClicker());
    }

    @Override
    public void onAttach() {
        Owlery.sendMessage(npc.getName() + " was assigned as an Owlery");
    }
}
