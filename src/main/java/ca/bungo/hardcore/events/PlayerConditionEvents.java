package ca.bungo.hardcore.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerConditionEvents implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();

        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attributeInstance == null) return;
        double health = attributeInstance.getBaseValue();
        if(health == 1) {
            player.sendMessage("&eYou are already at the lowest health possible... You have been spared this day.".convertToComponent());
            return;
        }

        attributeInstance.setBaseValue(health-1);
        player.sendMessage(("&4You have lost 1 heart! You are now at &e" + attributeInstance.getBaseValue()).convertToComponent());
    }

}
