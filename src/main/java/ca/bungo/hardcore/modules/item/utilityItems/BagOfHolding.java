package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import ca.bungo.hardcore.utility.pdc.InventoryPDC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BagOfHolding extends ItemModule implements CraftableModule {

    private static class BagOfHoldingHolder implements InventoryHolder {
        private final Inventory bagInventory;

        public BagOfHoldingHolder(){
            bagInventory = Bukkit.createInventory(this, 36, "&eBag of Holding".convertToComponent());
        }

        @Override
        public @NotNull Inventory getInventory() {
            return bagInventory;
        }
    }

    private InventoryPDC customPDCType;
    private NamespacedKey bagKey;
    private NamespacedKey avoidStack;

    public BagOfHolding(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("bagOfHolding");
        this.castingKey = "bag-of-holding";
        customPDCType = new InventoryPDC();
        bagKey = new NamespacedKey(Hardcore.instance, "bag-of-holding-data");
        avoidStack = new NamespacedKey(Hardcore.instance, "no-stacking");
    }

    public ItemStack[] getInventoryContents(ItemStack item){
        Inventory inventory = (Inventory) ItemStackBuilder.getCustomPDC(bagKey, item, customPDCType);
        if(inventory == null) return null;
        return inventory.getContents();
    }

    public void storeInventoryContents(ItemStack item, Inventory inventory){
        ItemStackBuilder builder = item.getItemStackBuilder();
        builder.addCustomPDC(bagKey, customPDCType, inventory);
        item.setItemMeta(builder.build().getItemMeta());
    }

    private void openBag(Player player){
        BagOfHoldingHolder bagOfHolding = new BagOfHoldingHolder();
        boolean isMainHand = verifyItem(player.getInventory().getItemInMainHand());
        bagOfHolding.getInventory().setContents(getInventoryContents(isMainHand ?
                player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand()));
        player.openInventory(bagOfHolding.getInventory());
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            openBag(event.getPlayer());
        }
    }

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("WLW", "LCL", "WLW")
                .setIngredient('W', new RecipeChoice.MaterialChoice(Tag.WOOL))
                .setIngredient('C', Material.CHEST)
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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        if(!(inventory.getHolder() instanceof BagOfHoldingHolder)) return;
        Player player = (Player) event.getPlayer();
        boolean isMainHand = verifyItem(player.getInventory().getItemInMainHand());
        ItemStack bag = isMainHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        storeInventoryContents(bag, inventory);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event){
        Inventory inventory = event.getView().getTopInventory();
        if(!(inventory.getHolder() instanceof BagOfHoldingHolder)) return;
        if(event.getCurrentItem() == null) return;
        if(!verifyItem(event.getCurrentItem())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPrepCraft(PrepareItemCraftEvent event){
        Recipe recipe = event.getRecipe();
        if(recipe == null) return;
        if(!verifyItem(recipe.getResult())) return;
        ItemStack bagItem = this.castingItem;
        ItemStackBuilder builder = bagItem.getItemStackBuilder();
        BagOfHoldingHolder bagHolder = new BagOfHoldingHolder();
        Inventory newInventory = bagHolder.getInventory();
        builder.addCustomPDC(bagKey, customPDCType, newInventory);
        builder.addPDC(avoidStack, UUID.randomUUID().toString());
        event.getInventory().setResult(builder.build());
    }
}
