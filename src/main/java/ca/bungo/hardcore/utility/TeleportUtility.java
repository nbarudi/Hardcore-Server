package ca.bungo.hardcore.utility;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.types.HardcorePlayer;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TeleportUtility implements Listener {

    private static class TeleportData {
        private boolean shouldComplete;
        private Sound sound;
        private int effectTask;

        public TeleportData(int task){
            this.effectTask = task;
            this.shouldComplete = true;
        }

        public void cancelTask(){
            if(effectTask == -1) return;
            Bukkit.getScheduler().cancelTask(effectTask);
        }
    }

    private final Map<String, TeleportData> teleportStatus = new HashMap<>();

    public void instantTeleportPlayer(Player player, Location location){
        if(teleportStatus.containsKey(player.getUniqueId().toString())){
            player.sendMessage(("&cYou are already teleporting!").convertToComponent());
            return;
        }
        teleportStatus.put(player.getUniqueId().toString(), new TeleportData(-1));
        spawnTeleportEffect(player, location, 0);
        player.teleport(location);
    }

    public boolean teleportWithDelay(Player player, Location location, int ticks){
        if(teleportStatus.containsKey(player.getUniqueId().toString())){
            player.sendMessage(("&cYou are already teleporting!").convertToComponent());
            return false;
        }
        player.sendMessage(("&eDon't move! You will be warped in &a" + ticks/20 + "&e seconds!").convertToComponent());
        teleportStatus.put(player.getUniqueId().toString(), new TeleportData(-1));
        spawnTeleportEffect(player, location, ticks);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () ->{
            if(player.isOnline() && teleportStatus.get(player.getUniqueId().toString()).shouldComplete){
                player.teleport(location);
                player.sendMessage(("You have been warped!").convertToComponent());
            }
            teleportStatus.remove(player.getUniqueId().toString());
        }, ticks);
        return true;
    }

    public void teleportWithDelaySound(Player player, Location location){
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        int time = 10;
        float pitch = 1;
        if(hardcorePlayer.hasModule("TeleportReducedCooldownTwoModule")){
            time = 5;
            pitch = 2;
        }
        else if(hardcorePlayer.hasModule("TeleportReducedCooldownOneModule")){
            time = 7;
            pitch = 1.43f;
        }

        int ticks = time*20;

        if(teleportWithDelay(player,location,ticks)){
            Sound sound = Sound.sound(Key.key("custom.teleport"), Sound.Source.PLAYER, 1, pitch);
            player.playSound(sound);
            teleportStatus.get(player.getUniqueId().toString()).sound = sound;
        }
    }

    private void spawnTeleportEffect(Player player, Location to, int ticks){
        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.color(Color.PURPLE);
        builder.count(30);
        builder.allPlayers();
        builder.offset(0.5, 1, 0.5);

        int time = ticks/5;

        int[] numLoops = {0, 0};
        Random random = new Random();
        teleportStatus.get(player.getUniqueId().toString()).effectTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () -> {
            numLoops[0]++;
            builder.color(random.nextInt(0, 255), random.nextInt(0, 255), random.nextInt(0, 255));
            builder.location(player.getLocation().add(0, 1, 0));
            builder.spawn();
            builder.location(to.clone().add(0, 1, 0));
            builder.spawn();

            if(numLoops[0] >= time){
                teleportStatus.get(player.getUniqueId().toString()).cancelTask();
            }
        }, 5, 5);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(!teleportStatus.containsKey(event.getPlayer().getUniqueId().toString()))
            return;
        if(event.getTo().distance(event.getFrom()) >= 0.15 && teleportStatus.get(event.getPlayer().getUniqueId().toString()).shouldComplete){
            event.getPlayer().sendMessage(("&cYou have moved and cancelled your teleport!").convertToComponent());
            teleportStatus.get(event.getPlayer().getUniqueId().toString()).shouldComplete = false;
            teleportStatus.get(event.getPlayer().getUniqueId().toString()).cancelTask();
            event.getPlayer().stopSound(teleportStatus.get(event.getPlayer().getUniqueId().toString()).sound);
        }
    }

}
