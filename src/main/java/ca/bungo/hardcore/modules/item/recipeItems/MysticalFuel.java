package ca.bungo.hardcore.modules.item.recipeItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class MysticalFuel extends ItemModule implements CraftableModule {

    public MysticalFuel(String moduleName) {
        super(moduleName);
        this.castingKey = "magic-fuel";
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
        this.cost = this.castingItem;
        this.costAmount = 1;
        this.hasCost = true;
        this.hasCooldown = true;
    }

    @Override
    public Recipe getItemRecipe() {
        return null;
    }

    @Override
    public List<Recipe> getMultiRecipe() {

        List<Recipe> results = new ArrayList<>();

        //8 Low + 1 Coal = 1 Fuel
        ItemStack result = this.castingItem.clone();
        result.setAmount(1);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "1-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"));
        recipe.setIngredient('X', Material.COAL);
        results.add(recipe);

        //8 Low + 1 Blaze Rod = 3 Fuel
        result = this.castingItem.clone();
        result.setAmount(3);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "2-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"));
        recipe.setIngredient('X', Material.BLAZE_ROD);
        results.add(recipe);

        //8 Low + 1 Lava Bucket = 5 Fuel
        result = this.castingItem.clone();
        result.setAmount(5);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "3-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"));
        recipe.setIngredient('X', Material.LAVA_BUCKET);
        results.add(recipe);

        //8 Med + 1 Coal = 2 Fuel
        result = this.castingItem.clone();
        result.setAmount(2);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "4-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('X', Material.COAL);
        results.add(recipe);

        //8 Med + 1 Blaze Rod = 6 Fuel
        result = this.castingItem.clone();
        result.setAmount(6);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "5-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('X', Material.BLAZE_ROD);
        results.add(recipe);

        //8 Med + 1 Lava Bucket = 10 Fuel
        result = this.castingItem.clone();
        result.setAmount(10);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "6-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('X', Material.LAVA_BUCKET);
        results.add(recipe);

        //8 High + 1 Coal = 2 Fuel
        result = this.castingItem.clone();
        result.setAmount(3);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "7-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('X', Material.COAL);
        results.add(recipe);

        //8 High + 1 Blaze Rod = 6 Fuel
        result = this.castingItem.clone();
        result.setAmount(9);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "8-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('X', Material.BLAZE_ROD);
        results.add(recipe);

        //8 High + 1 Lava Bucket = 10 Fuel
        result = this.castingItem.clone();
        result.setAmount(15);
        recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "9-crafting-" + this.castingKey),
                result);
        recipe.shape("&&&", "&X&", "&&&");
        recipe.setIngredient('&', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('X', Material.LAVA_BUCKET);
        results.add(recipe);

        return results;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("1-crafting-" + this.castingKey,
                "2-crafting-" + this.castingKey,
                "3-crafting-" + this.castingKey,
                "4-crafting-" + this.castingKey,
                "5-crafting-" + this.castingKey,
                "6-crafting-" + this.castingKey,
                "7-crafting-" + this.castingKey,
                "8-crafting-" + this.castingKey,
                "9-crafting-" + this.castingKey);
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        event.getPlayer().setFireTicks(20);
        event.getPlayer().sendMessage(("&6Warning! This item is &4&lVERY &r&6volatile!").convertToComponent());
        event.getPlayer().getHardcorePlayer().addCooldown(this.getModuleName(), 1, this.castingItem);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1f, 0.3f);
    }

    @Override
    public boolean requiresModuleToCreate() { return true; }

    @Override
    public String overrideModuleName() { return "SimpleMagicTools"; }
}
