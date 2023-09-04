package ca.bungo.hardcore.modules.types.interfaces;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityInteractEvent;

public interface EntityInteractModule {

    @EventHandler
    void onEntityInteract(EntityInteractEvent event);

}
