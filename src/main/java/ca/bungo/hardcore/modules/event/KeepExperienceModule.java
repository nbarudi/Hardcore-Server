package ca.bungo.hardcore.modules.event;

import ca.bungo.hardcore.modules.types.classes.EventModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepExperienceModule extends EventModule implements BuyableModule {

    public KeepExperienceModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public String friendlyName() {
        return "Keep Levels";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String friendlyDescription() {
        return "Keep your experience on death!";
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(hardcorePlayer.hasModule(this.getModuleName())){
            event.setKeepLevel(true);
            event.setShouldDropExperience(false);
        }
    }
}
