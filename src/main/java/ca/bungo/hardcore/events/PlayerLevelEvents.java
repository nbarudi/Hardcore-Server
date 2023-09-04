package ca.bungo.hardcore.events;

import ca.bungo.hardcore.events.custom.PlayerLevelDownEvent;
import ca.bungo.hardcore.events.custom.PlayerLevelUpEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLevelEvents implements Listener {

    @EventHandler
    public void onLevelup(PlayerLevelUpEvent event){
        Player player = event.getPlayer().getBukkitPlayer();
        player.playEffect(EntityEffect.ENTITY_POOF);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.showTitle(Title.title(("&aYou have Leveled Up!").convertToComponent(),
                ("&eYou are now level " + event.getNewLevel()).convertToComponent()));
    }

    @EventHandler
    public void onLevelDown(PlayerLevelDownEvent event){
        Player player = event.getPlayer().getBukkitPlayer();
        player.playEffect(EntityEffect.ENTITY_POOF);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 0.75f);
        player.showTitle(Title.title(("&4Your Level was Reduced!").convertToComponent(),
                ("&eYou are now level " + event.getNewLevel()).convertToComponent()));
    }

}
