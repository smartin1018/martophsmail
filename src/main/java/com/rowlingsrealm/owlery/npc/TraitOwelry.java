package com.rowlingsrealm.owlery.npc;

import com.rowlingsrealm.owlery.Owelry;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class TraitOwelry extends Trait {

    public TraitOwelry() {
        super("Owelry");
    }

    @EventHandler
    public void click(NPCClickEvent event) {
        if (event.getNPC() != npc)
            return;

        Owelry.getCentralManager().getMailManager().openOwelry(event.getClicker());
    }

    @Override
    public void onAttach() {
        Owelry.sendMessage(npc.getName() + " was assigned as an Owelry");
    }
}
