package ca.bungo.hardcore.events;


import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class PlayerConnectionEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.registerHardcorePlayer();

        event.joinMessage(("&eWelcome &#fc5203" + player.getName() + " &eto hell!").convertToComponent());

        for(String moduleName : Hardcore.instance.moduleManager.getModuleNames()){
            Module module = Hardcore.instance.moduleManager.getModuleByName(moduleName);
            if(module instanceof ItemModule itemModule && itemModule instanceof CraftableModule){
                for(String key : ((CraftableModule) itemModule).getCraftingKeys()){
                    player.discoverRecipe(Objects.requireNonNull(NamespacedKey.fromString(key, Hardcore.instance)));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        event.quitMessage(("&#fc5203" + player.getName() + "&e has been freed from torment!").convertToComponent());
    }

}
