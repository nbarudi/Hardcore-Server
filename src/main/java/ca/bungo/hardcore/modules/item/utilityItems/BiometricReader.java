package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class BiometricReader extends ItemModule implements CraftableModule {

    private final ItemStack biometricCard;

    public BiometricReader(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("biometricReader");
        this.castingKey = "biometric-reader";
        this.hasCooldown = true;
        this.hasCost = true;
        this.cost = new ItemStack(Material.DIAMOND);
        this.costAmount = 2;
        this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
        this.biometricCard = Hardcore.instance.customItemManager.getCustomItem("biometric");
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        event.getPlayer().getHardcorePlayer().addCooldown(this.getModuleName(), 10, this.castingItem);

        ItemStackBuilder builder = new ItemStackBuilder(Material.AIR);
        builder.from(this.biometricCard);
        builder.setName("<!i>&e"+ event.getPlayer().getName() + "'s Biometric");
        builder.addPDC(NamespacedKey.fromString("biometric", Hardcore.instance), event.getPlayer().getUniqueId().toString());
        builder.setPlayerHead(event.getPlayer().getUniqueId().toString());
        event.getPlayer().getInventory().addItem(builder.build());
        event.getPlayer().sendMessage("&eCreated your Biometric Reading!".convertToComponent());
        event.getPlayer().playSound(Sound.sound(Key.key("block.note_block.guitar"), Sound.Source.PLAYER, 1, 0.5f));
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.getCastingKey()), this.castingItem);
        recipe.shape("MSM", "SLS", "MSM");
        recipe.setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('S', Material.DEEPSLATE_BRICKS);
        recipe.setIngredient('L', Material.GLASS);
        return recipe;
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
        return List.of("crafting-" + this.getCastingKey());
    }
}
