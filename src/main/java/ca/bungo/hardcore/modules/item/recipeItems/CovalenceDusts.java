package ca.bungo.hardcore.modules.item.recipeItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

public class CovalenceDusts {

    public static class LowCovalenceDust extends ItemModule implements CraftableModule {
        public LowCovalenceDust(String moduleName) {
            super(moduleName);
            this.castingKey = "low-coval-dust";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("lowCovalDust");
        }

        @Override
        public Recipe getItemRecipe() {
            ItemStack resultItem = this.castingItem.clone();
            resultItem.setAmount(20);
            ShapelessRecipe recipe = new ShapelessRecipe(
                    new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey),
                    resultItem);

            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.COBBLESTONE);
            recipe.addIngredient(Material.CHARCOAL);
            recipe.getResult().setAmount(5);
            return recipe;
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            event.getPlayer().sendMessage(("&#296e16You feel some magic radiating from this dust...").convertToComponent());
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.castingKey);
        }

        @Override
        public boolean requiresModuleToCreate() { return true; }

        @Override
        public String overrideModuleName() { return "SimpleMagicTools"; }
    }

    public static class MedCovalenceDust extends ItemModule implements CraftableModule {
        public MedCovalenceDust(String moduleName) {
            super(moduleName);
            this.castingKey = "med-coval-dust";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("medCovalDust");
        }

        @Override
        public Recipe getItemRecipe() {
            ItemStack resultItem = this.castingItem.clone();
            resultItem.setAmount(20);
            ShapelessRecipe recipe = new ShapelessRecipe(
                    new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey),
                    resultItem);
            recipe.addIngredient(Material.IRON_INGOT);
            recipe.addIngredient(Material.REDSTONE);
            recipe.getResult().setAmount(3);
            return recipe;
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            event.getPlayer().sendMessage(("&#23917bYou feel some magic radiating from this dust...").convertToComponent());
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.castingKey);
        }

        @Override
        public boolean requiresModuleToCreate() { return true; }

        @Override
        public String overrideModuleName() { return "StandardMagicTools"; }
    }

    public static class HighCovalenceDust extends ItemModule implements CraftableModule {
        public HighCovalenceDust(String moduleName) {
            super(moduleName);
            this.castingKey = "high-coval-dust";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("highCovalDust");
        }

        @Override
        public Recipe getItemRecipe() {
            ItemStack resultItem = this.castingItem.clone();
            resultItem.setAmount(1);
            ShapelessRecipe recipe = new ShapelessRecipe(
                    new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey),
                    resultItem);
            recipe.addIngredient(Material.DIAMOND);
            recipe.addIngredient(Material.COAL);
            return recipe;
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            event.getPlayer().sendMessage(("&#152a8aYou feel some magic radiating from this dust...").convertToComponent());
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.castingKey);
        }

        @Override
        public boolean requiresModuleToCreate() { return true; }

        @Override
        public String overrideModuleName() { return "AdvancedMagicTools"; }
    }

}
