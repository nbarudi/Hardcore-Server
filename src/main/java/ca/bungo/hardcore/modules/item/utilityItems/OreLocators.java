package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.modules.utility.XRayUtility;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class OreLocators {

    protected static ItemStack fuelCost = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
    protected static String locatorName = "ORE-LOCATE-ALL";

    public static class Tier1 extends ItemModule implements CraftableModule {

        public Tier1(String moduleName) {
            super(moduleName);
            this.cost = fuelCost;
            this.costAmount = 4;
            this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                    .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
            this.hasCooldown = true;
            this.hasCost = true;
            this.castingKey = "ore-locate-1";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("oreLocator1");
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
                return;

            Player player = event.getPlayer();
            HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
            hardcorePlayer.addCooldown(locatorName, 25, this.castingItem);
            XRayUtility.giveTempXrayToPlayer(player, 200, 4);
            player.sendMessage("&eYou can now see the ores within 4 blocks of you!".convertToComponent());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
        }

        @Override
        public Recipe getItemRecipe() {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem);
            recipe.shape("CDC",
                         "EXE",
                         "CDC");
            recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"));
            recipe.setIngredient('D', Material.DIAMOND_BLOCK);
            recipe.setIngredient('E', Material.EMERALD_BLOCK);
            recipe.setIngredient('X', Material.COMPASS);
            return recipe;
        }

        @Override
        protected boolean passesCooldown(Player player) {
            return OreLocators.passesCooldown(player);
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

    public static class Tier2 extends ItemModule implements CraftableModule {

        public Tier2(String moduleName) {
            super(moduleName);
            this.cost = fuelCost;
            this.costAmount = 8;
            this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                    .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
            this.hasCooldown = true;
            this.hasCost = true;
            this.castingKey = "ore-locate-2";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("oreLocator2");
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
                return;

            Player player = event.getPlayer();
            HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
            hardcorePlayer.addCooldown(locatorName, 20, this.castingItem);
            XRayUtility.giveTempXrayToPlayer(player, 200, 7);
            player.sendMessage("&eYou can now see the ores within 7 blocks of you!".convertToComponent());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
        }

        @Override
        public Recipe getItemRecipe() {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem);
            recipe.shape("CEC",
                    "SXS",
                    "CEC");
            recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
            recipe.setIngredient('S', Material.NETHER_STAR);
            recipe.setIngredient('E', Material.EMERALD_BLOCK);
            recipe.setIngredient('X', Hardcore.instance.customItemManager.getCustomItem("oreLocator1"));
            return recipe;
        }

        @Override
        protected boolean passesCooldown(Player player) {
            return OreLocators.passesCooldown(player);
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

    public static class Tier3 extends ItemModule implements CraftableModule {

        public Tier3(String moduleName) {
            super(moduleName);
            this.cost = fuelCost;
            this.costAmount = 12;
            this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                    .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
            this.hasCooldown = true;
            this.hasCost = true;
            this.castingKey = "ore-locate-3";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("oreLocator3");
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
                return;

            Player player = event.getPlayer();
            HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
            hardcorePlayer.addCooldown(locatorName, 15, this.castingItem);
            XRayUtility.giveTempXrayToPlayer(player, 200, 10);
            player.sendMessage("&eYou can now see the ores within 10 blocks of you!".convertToComponent());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
        }

        @Override
        public Recipe getItemRecipe() {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem);
            recipe.shape("CSC",
                    "SXS",
                    "CSC");
            recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
            recipe.setIngredient('S', Material.NETHER_STAR);
            recipe.setIngredient('X', Hardcore.instance.customItemManager.getCustomItem("oreLocator2"));
            return recipe;
        }

        @Override
        protected boolean passesCooldown(Player player) {
            return OreLocators.passesCooldown(player);
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

    protected static boolean passesCooldown(Player player){
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(hardcorePlayer.onCooldown(locatorName)){
            player.sendMessage(hardcorePlayer.getCooldownComponent(locatorName));
            return false;
        }
        return true;
    }

}
