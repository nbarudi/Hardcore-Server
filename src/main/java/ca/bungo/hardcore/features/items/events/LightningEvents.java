package ca.bungo.hardcore.features.items.events;

import ca.bungo.hardcore.Hardcore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;

public class LightningEvents implements Listener {

    public ItemStack bottledLightning;

    public LightningEvents(){
        bottledLightning = Hardcore.instance.customItemManager.getCustomItem("lightningInABottle");
    }

    @EventHandler
    public void onStrike(LightningStrikeEvent event){
        Location location = event.getLightning().getLocation();
        Block block = location.getBlock();

        if(block.getType().equals(Material.AIR) && block.getRelative(BlockFace.DOWN).getType().equals(Material.LIGHTNING_ROD))
            block = block.getRelative(BlockFace.DOWN);

        if(block.getType().equals(Material.LIGHTNING_ROD)){
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            if(blockBelow.getType().equals(Material.GLASS)){
                blockBelow.setType(Material.AIR);
                Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> location.getWorld().dropItem(blockBelow.getLocation(), bottledLightning), 20);
            }
        }
    }

}
