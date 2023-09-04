package ca.bungo.hardcore.modules.types.classes;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.managers.PlayerManager;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.types.HardcorePlayer;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class EventModule extends Module implements Listener {

    protected Component cooldownEndMessage;

    public EventModule(String moduleName) {
        super(moduleName);
    }

    @Override
    protected boolean passesCooldown(Player player) {
        if(!this.hasCooldown) return true;
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        return !hardcorePlayer.onCooldown(this.getModuleName());
    }

    public boolean canRun(Player player){
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        return hardcorePlayer.hasModule(this.getModuleName()) && this.passesCooldown(player) && this.passesCost(player);
    }

    @EventHandler
    public void onTick(ServerTickEndEvent event){
        if(cooldownEndMessage == null) return;

        for(HardcorePlayer player : PlayerManager.playerList.values()){
            if(player.hasModule(this.getModuleName())){
                if(player.containsCooldown(this.getModuleName()) && !player.onCooldown(this.getModuleName()))
                    player.getBukkitPlayer().sendMessage(cooldownEndMessage);
            }
        }

    }

}
