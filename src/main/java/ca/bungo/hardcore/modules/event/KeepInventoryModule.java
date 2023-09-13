package ca.bungo.hardcore.modules.event;

import ca.bungo.hardcore.modules.types.classes.EventModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepInventoryModule extends EventModule implements BuyableModule {

    public KeepInventoryModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public String friendlyName() {
        return "Keep Inventory";
    }

    @Override
    public String depends() {
        return "KeepExperienceModule";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String friendlyDescription() {
        return "Keep your inventory on death!";
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(hardcorePlayer.hasModule(this.getModuleName())){
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }
}
