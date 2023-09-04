package ca.bungo.hardcore.modules.item.armorModifiers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ArmorModifiers {

    protected static final ItemStack dummyItem = new ItemStack(Material.DIRT);

    public static class ArmorSpeedModifier extends ItemModule implements CraftableModule {
        private final NamespacedKey speedModKey = new NamespacedKey(Hardcore.instance, "boots-speed-mod-modifier");

        private final AttributeModifier lowModifier = new AttributeModifier(UUID.fromString("4851c381-7bdd-4da3-8d7a-03041f17e128"), "generic.movement_speed", 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        private final AttributeModifier medModifier = new AttributeModifier(UUID.fromString("4851c381-7bdd-4da3-8d7a-03041f17e128"), "generic.movement_speed", 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        private final AttributeModifier highModifier = new AttributeModifier(UUID.fromString("4851c381-7bdd-4da3-8d7a-03041f17e128"), "generic.movement_speed", 4, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        public ArmorSpeedModifier(String moduleName) {
            super(moduleName);
            this.castingKey = "armor-speed-mod";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("armorSpeedMod");
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {}

        @Override
        public Recipe getItemRecipe() {
            return null;
        }

        @Override
        public List<Recipe> getMultiRecipe() {
            SmithingTransformRecipe smithingRecipe = new SmithingTransformRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.getCastingKey()),
                    dummyItem, new RecipeChoice.ExactChoice(this.castingItem),
                    new RecipeChoice.MaterialChoice(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS),
                    new RecipeChoice.ExactChoice(Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"),
                            Hardcore.instance.customItemManager.getCustomItem("medCovalDust"),
                            Hardcore.instance.customItemManager.getCustomItem("highCovalDust")));

            ShapedRecipe craftingRecipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-item-" + this.getCastingKey()), this.castingItem);
            craftingRecipe.shape(
                    "SFS",
                    "FNF",
                    "SFS");
            craftingRecipe.setIngredient('S', Material.SUGAR);
            craftingRecipe.setIngredient('F', Hardcore.instance.customItemManager.getCustomItem("fuelItem"));
            craftingRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            return List.of(smithingRecipe, craftingRecipe);
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.getCastingKey(), "crafting-item-" + this.getCastingKey());
        }

        @EventHandler
        public void onSmithingCraft(PrepareSmithingEvent event) {
            if(event.getInventory().containsAtLeast(this.castingItem, 1)){
                ItemStack armorItem = event.getInventory().getItem(1);
                if(armorItem == null) return;
                ItemStackBuilder clonedItem = armorItem.getItemStackBuilder();
                if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"), 1)){
                    clonedItem.addPDC(this.speedModKey, "lowCovalDust");
                    clonedItem.addLore("<!i>&#296e16x2 Movement Speed");
                }
                else if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("medCovalDust"), 1)){
                    clonedItem.addPDC(this.speedModKey, "medCovalDust");
                    clonedItem.addLore("<!i>&#23917bx3 Movement Speed");
                }
                if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("highCovalDust"), 1)){
                    clonedItem.addPDC(this.speedModKey, "highCovalDust");
                    clonedItem.addLore("<!i>&#152a8ax4 Movement Speed");
                }
                event.setResult(clonedItem.build());
            }
        }

        @EventHandler
        public void onEquip(PlayerArmorChangeEvent event) {
            Player player = event.getPlayer();

            ItemStack old = event.getOldItem();
            ItemStack newArmor = event.getNewItem();

            if(old != null){
                if(old.getItemMeta() != null){
                    String container = old.getItemMeta().getPersistentDataContainer().get(this.speedModKey, PersistentDataType.STRING);
                    if(container != null){
                        if(container.equalsIgnoreCase("lowCovalDust")) {
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(lowModifier);
                        }
                        else if(container.equalsIgnoreCase("medCovalDust")){
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(medModifier);
                        }
                        else if(container.equalsIgnoreCase("highCovalDust")){
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(highModifier);
                        }
                    }
                }
            }

            if(newArmor != null){
                if(newArmor.getItemMeta() != null){
                    String container = newArmor.getItemMeta().getPersistentDataContainer().get(this.speedModKey, PersistentDataType.STRING);
                    if(container != null){
                        if(container.equalsIgnoreCase("lowCovalDust")) {
                            if(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers().contains(lowModifier)) return;
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(lowModifier);
                        }
                        else if(container.equalsIgnoreCase("medCovalDust")){
                            if(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers().contains(medModifier)) return;
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(medModifier);
                        }
                        else if(container.equalsIgnoreCase("highCovalDust")){
                            if(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers().contains(highModifier)) return;
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(highModifier);
                        }
                    }
                }
            }
        }
    }

    public static class ArmorKnockbackResistModifier extends ItemModule implements CraftableModule {
        private final NamespacedKey knockbackModKey = new NamespacedKey(Hardcore.instance, "leggings-knockback-mod-modifier");

        private final AttributeModifier lowModifier = new AttributeModifier(UUID.fromString("285805cd-f5bb-4841-b347-ca15eb7fcfa2"), "generic.knockback_resistance", 0.5, AttributeModifier.Operation.ADD_NUMBER);
        private final AttributeModifier medModifier = new AttributeModifier(UUID.fromString("285805cd-f5bb-4841-b347-ca15eb7fcfa2"), "generic.knockback_resistance", 0.75, AttributeModifier.Operation.ADD_NUMBER);
        private final AttributeModifier highModifier = new AttributeModifier(UUID.fromString("285805cd-f5bb-4841-b347-ca15eb7fcfa2"), "generic.knockback_resistance", 1, AttributeModifier.Operation.ADD_NUMBER);
        public ArmorKnockbackResistModifier(String moduleName) {
            super(moduleName);
            this.castingKey = "armor-knockback-mod";
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("armorKnockbackMod");
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {}

        @Override
        public Recipe getItemRecipe() {
            return null;
        }

        @Override
        public List<Recipe> getMultiRecipe() {
            SmithingTransformRecipe smithingRecipe = new SmithingTransformRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.getCastingKey()),
                    dummyItem, new RecipeChoice.ExactChoice(this.castingItem),
                    new RecipeChoice.MaterialChoice(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS),
                    new RecipeChoice.ExactChoice(Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"),
                            Hardcore.instance.customItemManager.getCustomItem("medCovalDust"),
                            Hardcore.instance.customItemManager.getCustomItem("highCovalDust")));

            ShapedRecipe craftingRecipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-item-" + this.getCastingKey()), this.castingItem);
            craftingRecipe.shape(
                    "SFS",
                    "FNF",
                    "SFS");
            craftingRecipe.setIngredient('S', Material.SLIME_BALL);
            craftingRecipe.setIngredient('F', Hardcore.instance.customItemManager.getCustomItem("fuelItem"));
            craftingRecipe.setIngredient('N', Material.NETHERITE_INGOT);
            return List.of(smithingRecipe, craftingRecipe);
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.getCastingKey(), "crafting-item-" + this.getCastingKey());
        }

        @EventHandler
        public void onSmithingCraft(PrepareSmithingEvent event) {
            if(event.getInventory().containsAtLeast(this.castingItem, 1)){
                ItemStack armorItem = event.getInventory().getItem(1);
                if(armorItem == null) return;
                ItemStackBuilder clonedItem = armorItem.getItemStackBuilder();
                if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"), 1)){
                    clonedItem.addPDC(this.knockbackModKey, "lowCovalDust");
                    clonedItem.addLore("<!i>&#296e1650% Knockback Resistance");
                }
                else if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("medCovalDust"), 1)){
                    clonedItem.addPDC(this.knockbackModKey, "medCovalDust");
                    clonedItem.addLore("<!i>&#23917b75% Knockback Resistance");
                }
                if(event.getInventory().containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("highCovalDust"), 1)){
                    clonedItem.addPDC(this.knockbackModKey, "highCovalDust");
                    clonedItem.addLore("<!i>&#152a8a100% Knockback Resistance 3");
                }
                event.setResult(clonedItem.build());
            }
        }

        @EventHandler
        public void onEquip(PlayerArmorChangeEvent event) {
            Player player = event.getPlayer();

            ItemStack old = event.getOldItem();
            ItemStack newArmor = event.getNewItem();

            if(old != null){
                if(old.getItemMeta() != null){
                    String container = old.getItemMeta().getPersistentDataContainer().get(this.knockbackModKey, PersistentDataType.STRING);
                    if(container != null){
                        if(container.equalsIgnoreCase("lowCovalDust")) {
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(lowModifier);
                        }
                        else if(container.equalsIgnoreCase("medCovalDust")){
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(medModifier);
                        }
                        else if(container.equalsIgnoreCase("highCovalDust")){
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(highModifier);
                        }
                    }
                }
            }

            if(newArmor != null){
                if(newArmor.getItemMeta() != null){
                    String container = newArmor.getItemMeta().getPersistentDataContainer().get(this.knockbackModKey, PersistentDataType.STRING);
                    if(container != null){
                        if(container.equalsIgnoreCase("lowCovalDust")) {
                            if(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getModifiers().contains(lowModifier)) return;
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(lowModifier);
                        }
                        else if(container.equalsIgnoreCase("medCovalDust")){
                            if(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getModifiers().contains(medModifier)) return;
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(medModifier);
                        }
                        else if(container.equalsIgnoreCase("highCovalDust")){
                            if(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getModifiers().contains(highModifier)) return;
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(highModifier);
                        }
                    }
                }
            }
        }
    }
}
