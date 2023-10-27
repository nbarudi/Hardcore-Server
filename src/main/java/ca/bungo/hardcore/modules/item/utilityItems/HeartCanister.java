package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class HeartCanister extends ItemModule implements CraftableModule {

    public HeartCanister(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("heartCanister");
        this.castingKey = "heart-canister";
        this.hasCost = true;
        this.cost = this.castingItem;
        this.costAmount = 1;
        this.autoTriggerCost = false;
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            Player player = event.getPlayer();
            AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if(attributeInstance == null) return; //This should be impossible

            attributeInstance.setBaseValue(attributeInstance.getBaseValue() + 2);
            triggerCost(player);
            player.sendMessage("&4You have gained a heart!".convertToComponent());
        }


    }

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("ISI", "MNM", "ISI")
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('N', Material.NETHER_STAR)
                .setIngredient('S', Hardcore.instance.customItemManager.getCustomItem("soulShard"))
                .setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "StandardMagicTools";
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.castingKey);
    }
}
