package ca.bungo.hardcore.modules.item.funItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class GrappleHook extends ItemModule implements CraftableModule {

    public GrappleHook(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("grappleHook");
        this.castingKey = "grapple-hook";
        this.hasCooldown = true;
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {}

    @EventHandler
    protected void fishEvent(PlayerFishEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if(!this.verifyItem(itemStack)) return;
        if(event.getState().equals(PlayerFishEvent.State.REEL_IN)){
            Location movement = event.getHook().getLocation().subtract(event.getPlayer().getLocation());
            event.getPlayer().setVelocity(movement.toVector().multiply(0.4));
            event.getPlayer().getHardcorePlayer().addCooldown(this.getModuleName(), 5, this.castingItem);
            event.getPlayer().sendMessage("&eWeeeeeeeee".convertToComponent());
        }
    }

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("LRL", "SFS", "LRL")
                .setIngredient('L', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"))
                .setIngredient('R', Material.REDSTONE)
                .setIngredient('S', Material.STRING)
                .setIngredient('F', Material.FISHING_ROD);
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "SimpleMagicTools";
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.castingKey);
    }
}
