package ca.bungo.hardcore.modules.types.interfaces;

import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public interface CraftableModule extends Listener {

        Recipe getItemRecipe();
        String getModuleName();

        default List<Recipe> getMultiRecipe() { return null; };

        default boolean requiresModuleToCreate() { return false; }
        default String overrideModuleName() { return null; }

        List<String> getCraftingKeys();

        default boolean canCraft(Player player, ItemStack itemStack){
            if(this.getItemRecipe() != null){
                if(!this.getItemRecipe().getResult().equals(itemStack))
                    return true;
            }
            else if(this.getMultiRecipe() != null){
                boolean _temp = false;
                for(Recipe recipe : this.getMultiRecipe()){
                    if(recipe.getResult().equals(itemStack))
                        _temp = true;
                }
                if(!_temp)
                    return true;
            }
            HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
            String expectedModule = this.getModuleName();
            if(this.overrideModuleName() != null){
                expectedModule = this.overrideModuleName();
            }
            return hardcorePlayer.hasModule(expectedModule);
        }

        @EventHandler
        default void onCraft(CraftItemEvent event) {
            if(!this.requiresModuleToCreate()) return;
            ItemStack craftedItem = event.getRecipe().getResult();
            Player crafter = (Player) event.getInventory().getHolder();
            if(crafter == null) return;
            if(!canCraft(crafter, craftedItem)){
                crafter.sendMessage(("&4You do not have the skills to create this item!").convertToComponent());
                crafter.closeInventory();
                event.setCancelled(true);
            }
        }
}
