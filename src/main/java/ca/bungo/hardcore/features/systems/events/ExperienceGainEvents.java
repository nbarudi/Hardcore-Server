package ca.bungo.hardcore.features.systems.events;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExperienceGainEvents implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(event.getTo().distance(event.getFrom()) > 0.25){
            hardcorePlayer.addExperience(0.5);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        hardcorePlayer.addExperience(2);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        hardcorePlayer.addExperience(2);
    }

    @EventHandler
    public void onAchievement(PlayerAdvancementDoneEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        hardcorePlayer.addExperience(100);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event){
        Entity killed = event.getEntity();
        Player player = event.getEntity().getKiller();
        if(player == null) return;
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(killed instanceof Monster){
            hardcorePlayer.addExperience(75);
        }
        else if(killed instanceof Player killedPlayer){
            HardcorePlayer hardcoreKilledPlayer = killedPlayer.getHardcorePlayer();
            hardcoreKilledPlayer.removeExperience(250);
            hardcorePlayer.addExperience(250);
        }
        else {
            hardcorePlayer.addExperience(25);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraft(CraftItemEvent event){
        Player player = (Player) event.getInventory().getHolder();
        if(player == null) return;
        if(!event.isCancelled()){
            HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
            hardcorePlayer.addExperience(2);
        }
    }

}
