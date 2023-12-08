package ca.bungo.hardcore.modules.event;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.EventModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpModule extends EventModule implements BuyableModule {


    public DoubleJumpModule(String moduleName) {
        super(moduleName);
        this.hasCooldown = true;
    }

    @Override
    public void onPurchase(Player player) {
        Bukkit.getScheduler().runTaskLater(Hardcore.instance, ()-> player.setAllowFlight(true), 10);
    }

    @Override
    public String friendlyName() {
        return "Double Jump";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String friendlyDescription() {
        return "Gain the ability to Double Jump!";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        if(event.getPlayer().getHardcorePlayer().hasModule(this.getModuleName())){
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(player.getGameMode().equals(GameMode.CREATIVE)) return;
        if(!this.canRun(player)){
            Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> player.setFlying(false), 1);
            player.sendMessage(hardcorePlayer.getCooldownComponent(this.getModuleName()));
            return;
        }
        Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> player.setFlying(false), 1);
        player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection()).add(new Vector(0,1,0)));
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.5f, 1f);
        hardcorePlayer.addCooldown(this.getModuleName(), 2, null);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(!hardcorePlayer.hasModule(this.getModuleName())) return;
        player.setAllowFlight(true);
    }
}
