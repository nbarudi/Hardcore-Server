package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerModTableBlock extends CustomBlockModule implements CraftableModule {

    public static Map<String, Inventory> playerSkillShops = new HashMap<>();

    public PlayerModTableBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("playerModTable"));
        this.blockKey = "playermodtable-block";
    }

    private void loadInventory(Player player){
        List<Module> buyable = Hardcore.instance.moduleManager.getBuyableModules();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        buyable.sort((a, b) ->{
            BuyableModule buyableModuleA = (BuyableModule) a;
            BuyableModule buyableModuleB = (BuyableModule) b;
            if(!hardcorePlayer.hasModule(buyableModuleA.depends()))
                return 1;
            if(hardcorePlayer.hasModule(buyableModuleA.getModuleName()))
                return -1;
            if(buyableModuleA.depends() == null){
                return -1;
            }
            else if(buyableModuleB.depends() == null){
                return 1;
            }
            if(buyableModuleA.depends().equals(buyableModuleB.getModuleName())){
                return 1;
            }
            if(buyableModuleA.depends().equals(buyableModuleB.depends())){
                return 0;
            }
            return -1;
        } );

        Inventory skillInventory = playerSkillShops.get(player.getUniqueId().toString());
        if(skillInventory == null)
            skillInventory = Bukkit.createInventory(player, 54, ("&2Skill Shop").convertToComponent());

        for(Module module : buyable){
            ItemStack moduleItem = createItem(hardcorePlayer, module);
            skillInventory.setItem(buyable.indexOf(module), moduleItem);
        }

        player.openInventory(skillInventory);
        playerSkillShops.put(player.getUniqueId().toString(), skillInventory);
    }

    private ItemStack createItem(HardcorePlayer player, Module module){
        ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER);
        BuyableModule asBuyable = (BuyableModule) module;

        if(!player.hasModule(asBuyable.depends())){
            //Missing Dependant Module
            builder.setType(Material.BEDROCK);
            builder.setName("<!i>&0&k" + asBuyable.friendlyName());
            builder.addLore("&eYou are missing knowledge to access this skill!");
        }
        else if(player.hasModule(module.getModuleName())){
            //Has purchased the module
            builder.setType(Material.GREEN_CONCRETE);
            builder.setName("<!i>&2" + asBuyable.friendlyName());
            builder.addLore("&a" + asBuyable.friendlyDescription());
            builder.addLore("&eYou have purchased this skill already!");
        }
        else if(player.hasPoints(asBuyable.getCost())){
            builder.setType(Material.LIME_CONCRETE);
            builder.setName("<!i>&a" + asBuyable.friendlyName());
            builder.addLore("&e" + asBuyable.friendlyDescription());
            builder.addLore("&cCost: " + asBuyable.getCost() + " points.");
            builder.addEnchantment(Enchantment.ARROW_INFINITE, 1);
            builder.addFlag(ItemFlag.HIDE_ENCHANTS);
        }
        else {
            builder.setType(Material.RED_CONCRETE);
            builder.setName("<!i>&4" + asBuyable.friendlyName());
            builder.addLore("&e" + asBuyable.friendlyDescription());
            builder.addLore("&cCost: " + asBuyable.getCost() + " points.");
        }
        builder.addPDC(this.itemKey, module.getModuleName());

        return builder.build();
    }

    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        //ToDo: Implement Skill Shop

        Player player = event.getPlayer();
        loadInventory(player);


    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.blockKey), this.blockItem);
        recipe.shape(
                "ROR",
                "GLG",
                "GGG");
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('O', Material.FURNACE);
        recipe.setIngredient('G', Material.STONE);
        recipe.setIngredient('L', Material.LIGHTNING_ROD);
        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.blockKey);
    }
}
