package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
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

import java.util.HashSet;
import java.util.List;

public class LumberAxe extends ItemModule implements CraftableModule {

    private static final BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
            BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    public LumberAxe(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("lumberAxe");
        this.castingKey = "lumber-axe";
    }

    private HashSet<Block> calculateLogs(Block startingBlock, HashSet<Block> blocks){
        if(!Tag.LOGS.getValues().contains(startingBlock.getType()) &&
                !Tag.LEAVES.getValues().contains(startingBlock.getType()))
            return blocks;
        blocks.add(startingBlock);
        for(BlockFace direction : directions){
            Block resultingBlock = startingBlock.getRelative(direction);
            if(blocks.contains(resultingBlock)) continue;
            calculateLogs(resultingBlock, blocks);
        }
        return  blocks;
    }

    private void breakTree(Block block, ItemStack mainHandItem){
        HashSet<Block> toBreak = calculateLogs(block, new HashSet<>());
        for(Block treeBlock : toBreak){
            treeBlock.breakNaturally(mainHandItem);
            treeBlock.getWorld().playSound(treeBlock.getLocation(), treeBlock.getBlockData().getSoundGroup().getBreakSound(),
                    1.0f, 1.0f);
        }
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {}


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if(!verifyItem(mainHandItem)) return;

        if(!Tag.LOGS.getValues().contains(block.getType())) return;

        breakTree(block, mainHandItem);
    }

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("LPL", "PSP", "LPL")
                .setIngredient('L', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"))
                .setIngredient('S', Hardcore.instance.customItemManager.getCustomItem("soulShard"))
                .setIngredient('P', Material.DIAMOND_AXE);
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
