package ca.bungo.hardcore.modules.event;

import ca.bungo.hardcore.modules.types.classes.EventModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RespawnEventModule extends EventModule implements BuyableModule {

    public RespawnEventModule(String moduleName) {
        super(moduleName);
        this.hasCooldown = true;
        this.cooldownEndMessage = ("&eYour ability to cheat death has been restored!").convertToComponent();
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String friendlyName() {
        return "Cheat Death";
    }

    @Override
    public String friendlyDescription() {
        return "You have the ability to cheat death!";
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        if(this.canRun(player)){
            event.setCancelled(true);
            player.setHealth(20);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
            player.sendMessage(("&aYou have cheated death because of your skills!").convertToComponent());
            hardcorePlayer.addCooldown(this.getModuleName(), 120, null);
        }

    }
}
