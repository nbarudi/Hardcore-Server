package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.types.timings.TickTimer;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Magnets {

    public static void magnetizeItems(Player player, List<Entity> items){
        for(Entity entity : items){
            Vector direct = entity.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(-0.5);
            entity.setVelocity(direct);
        }
    }

    public static NamespacedKey avoidStack = new NamespacedKey(Hardcore.instance, "no-stacking");;
    public static NamespacedKey isEnabled = new NamespacedKey(Hardcore.instance, "is-enabled");

    public static class LowTierMagnet extends ItemModule implements CraftableModule {

        public LowTierMagnet(String moduleName) {
            super(moduleName);
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("lowTierMagnet");
            this.castingKey = "low-tier-magnet";
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            && event.getPlayer().isSneaking()){
                toggleMagnet(event.getPlayer());
            }
        }

        @TickTimer
        private void onTick(){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(hasMagnetEnabled(player)){
                    List<Entity> nearbyItems = new ArrayList<>();
                    List<Entity> nearByEntities = player.getNearbyEntities(3, 3, 3);
                    if(nearByEntities.isEmpty()) continue;

                    for(Entity entity : nearByEntities){
                        if(entity instanceof Item)
                            nearbyItems.add(entity);
                    }
                    magnetizeItems(player, nearbyItems);
                }
            }
        }

        private boolean hasMagnetEnabled(Player player){
            for(ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack == null) continue;
                if(verifyItem(itemStack))
                    return isMagnetEnabled(itemStack);
            }
            return false;
        }

        private boolean isMagnetEnabled(ItemStack magnet){
            if(!verifyItem(magnet)) return false;
            return (boolean) ItemStackBuilder.getCustomPDC(isEnabled, magnet, PersistentDataType.BOOLEAN);
        }

        private void toggleMagnet(Player player){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemStackBuilder builder = itemStack.getItemStackBuilder();
            if(!verifyItem(itemStack)) return;

            boolean enabled = isMagnetEnabled(itemStack);
            if(enabled){
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
                builder.removeEnchantment(Enchantment.ARROW_INFINITE);
                player.sendMessage("&4Disabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }else{
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, true);
                builder.addEnchantment(Enchantment.ARROW_INFINITE, -1);
                player.sendMessage("&aEnabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }
            itemStack.setItemMeta(builder.build().getItemMeta());
        }

        @EventHandler
        public void onPrepCraft(PrepareItemCraftEvent event){
            Recipe recipe = event.getRecipe();
            if(recipe == null) return;
            if(!verifyItem(recipe.getResult())) return;
            ItemStack bagItem = this.castingItem;
            ItemStackBuilder builder = bagItem.getItemStackBuilder();
            builder.addPDC(avoidStack, UUID.randomUUID().toString());
            builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
            event.getInventory().setResult(builder.build());
        }

        @Override
        public Recipe getItemRecipe() {
            return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                    .shape("LHL", "ISI", "LHL")
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('H', Material.HOPPER)
                    .setIngredient('S', Hardcore.instance.customItemManager.getCustomItem("soulShard"))
                    .setIngredient('L', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"));
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

    public static class MedTierMagnet extends ItemModule implements CraftableModule {

        public MedTierMagnet(String moduleName) {
            super(moduleName);
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("medTierMagnet");
            this.castingKey = "med-tier-magnet";
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    && event.getPlayer().isSneaking()){
                toggleMagnet(event.getPlayer());
            }
        }

        @TickTimer
        private void onTick(){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(hasMagnetEnabled(player)){
                    List<Entity> nearbyItems = new ArrayList<>();
                    List<Entity> nearByEntities = player.getNearbyEntities(6, 6, 6);
                    if(nearByEntities.isEmpty()) continue;

                    for(Entity entity : nearByEntities){
                        if(entity instanceof Item)
                            nearbyItems.add(entity);
                    }
                    magnetizeItems(player, nearbyItems);
                }
            }
        }

        private boolean hasMagnetEnabled(Player player){
            for(ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack == null) continue;
                if(verifyItem(itemStack))
                    return isMagnetEnabled(itemStack);
            }
            return false;
        }

        private boolean isMagnetEnabled(ItemStack magnet){
            if(!verifyItem(magnet)) return false;
            return (boolean) ItemStackBuilder.getCustomPDC(isEnabled, magnet, PersistentDataType.BOOLEAN);
        }

        private void toggleMagnet(Player player){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemStackBuilder builder = itemStack.getItemStackBuilder();
            if(!verifyItem(itemStack)) return;

            boolean enabled = isMagnetEnabled(itemStack);
            if(enabled){
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
                builder.removeEnchantment(Enchantment.ARROW_INFINITE);
                player.sendMessage("&4Disabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }else{
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, true);
                builder.addEnchantment(Enchantment.ARROW_INFINITE, -1);
                player.sendMessage("&aEnabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }
            itemStack.setItemMeta(builder.build().getItemMeta());
        }

        @EventHandler
        public void onPrepCraft(PrepareItemCraftEvent event){
            Recipe recipe = event.getRecipe();
            if(recipe == null) return;
            if(!verifyItem(recipe.getResult())) return;
            ItemStack bagItem = this.castingItem;
            ItemStackBuilder builder = bagItem.getItemStackBuilder();
            builder.addPDC(avoidStack, UUID.randomUUID().toString());
            builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
            event.getInventory().setResult(builder.build());
        }

        @Override
        public Recipe getItemRecipe() {
            return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                    .shape("DHD", "IMI", "DHD")
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('H', Material.HOPPER)
                    .setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("lowTierMagnet"))
                    .setIngredient('D', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
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

    public static class HighTierMagnet extends ItemModule implements CraftableModule {

        public HighTierMagnet(String moduleName) {
            super(moduleName);
            this.castingItem = Hardcore.instance.customItemManager.getCustomItem("highTierMagnet");
            this.castingKey = "high-tier-magnet";
        }

        @Override
        protected void runItemAbility(PlayerInteractEvent event) {
            if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    && event.getPlayer().isSneaking()){
                toggleMagnet(event.getPlayer());
            }
        }

        @TickTimer
        private void onTick(){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(hasMagnetEnabled(player)){
                    List<Entity> nearByEntities = player.getNearbyEntities(6, 6, 6);
                    if(nearByEntities.isEmpty()) continue;

                    for(Entity entity : nearByEntities){
                        if(entity instanceof Item item){
                            item.teleportAsync(player.getLocation());
                        }
                    }
                }
            }
        }

        private boolean hasMagnetEnabled(Player player){
            for(ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack == null) continue;
                if(verifyItem(itemStack))
                    return isMagnetEnabled(itemStack);
            }
            return false;
        }

        private boolean isMagnetEnabled(ItemStack magnet){
            if(!verifyItem(magnet)) return false;
            return (boolean) ItemStackBuilder.getCustomPDC(isEnabled, magnet, PersistentDataType.BOOLEAN);
        }

        private void toggleMagnet(Player player){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemStackBuilder builder = itemStack.getItemStackBuilder();
            if(!verifyItem(itemStack)) return;

            boolean enabled = isMagnetEnabled(itemStack);
            if(enabled){
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
                builder.removeEnchantment(Enchantment.ARROW_INFINITE);
                player.sendMessage("&4Disabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }else{
                builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, true);
                builder.addEnchantment(Enchantment.ARROW_INFINITE, -1);
                player.sendMessage("&aEnabled &emagnet!".convertToComponent());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            }
            itemStack.setItemMeta(builder.build().getItemMeta());
        }

        @EventHandler
        public void onPrepCraft(PrepareItemCraftEvent event){
            Recipe recipe = event.getRecipe();
            if(recipe == null) return;
            if(!verifyItem(recipe.getResult())) return;
            ItemStack bagItem = this.castingItem;
            ItemStackBuilder builder = bagItem.getItemStackBuilder();
            builder.addPDC(avoidStack, UUID.randomUUID().toString());
            builder.addCustomPDC(isEnabled, PersistentDataType.BOOLEAN, false);
            event.getInventory().setResult(builder.build());
        }

        @Override
        public Recipe getItemRecipe() {
            return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                    .shape("DHD", "IMI", "DHD")
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('H', Material.HOPPER)
                    .setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medTierMagnet"))
                    .setIngredient('D', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        }

        @Override
        public boolean requiresModuleToCreate() {
            return true;
        }

        @Override
        public String overrideModuleName() {
            return "AdvancedMagicTools";
        }

        @Override
        public List<String> getCraftingKeys() {
            return List.of("crafting-" + this.castingKey);
        }
    }

}
