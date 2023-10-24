package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class TunnelBore extends ItemModule implements CraftableModule {

    List<Material> blockBlacklist = new ArrayList<>();

    public TunnelBore(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("tunnelBore");
        this.castingKey = "tunnel-bore";
        loadBlacklist();
    }

    private void loadBlacklist(){
        //ToDo: There are MANY blacklists to add and I dont want to do it all yet but eventually I will
        blockBlacklist.addAll(Tag.LOGS.getValues());
        blockBlacklist.addAll(Tag.LEAVES.getValues());
        blockBlacklist.addAll(Tag.PLANKS.getValues());
        blockBlacklist.addAll(Tag.WOOL.getValues());

    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if(!verifyItem(mainHandItem)) return;

        break3x3x3(block, mainHandItem);
    }

    private void break3x3x3(Block baseBlock, ItemStack heldItem){
        Location baseLocation = baseBlock.getLocation().clone();
        for(int x = -1; x < 2; x++){
            for(int y = -1; y < 2; y++){
                for(int z = -1; z < 2; z++){
                    Location location = new Location(baseLocation.getWorld(), baseLocation.getX() + x,
                            baseLocation.getY() + y, baseLocation.getZ() + z);
                    Block toBreak = location.getBlock();
                    if(toBreak.getType().getHardness() <= 0 || toBreak.getType().getHardness() >= 50.0f) continue;
                    if(blockBlacklist.contains(toBreak.getType())) continue;

                    if(toBreak.getType().equals(Material.AIR)) continue;
                    toBreak.breakNaturally(heldItem);
                    toBreak.getWorld().playSound(toBreak.getLocation(), toBreak.getBlockData().getSoundGroup().getBreakSound(),
                            1.0f, 1.0f);
                }
            }
        }
    }


    @Override
    protected void runItemAbility(PlayerInteractEvent event) {}

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("LPL", "PSP", "LPL")
                .setIngredient('L', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"))
                .setIngredient('S', Hardcore.instance.customItemManager.getCustomItem("soulShard"))
                .setIngredient('P', Material.DIAMOND_PICKAXE);
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
