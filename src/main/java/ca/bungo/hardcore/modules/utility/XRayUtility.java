package ca.bungo.hardcore.modules.utility;

import ca.bungo.hardcore.Hardcore;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XRayUtility implements Listener {
    static HashMap<Material, NamedTextColor> oreToTeam = new HashMap<>();
    static Map<Block, Slime> slimeStorage = new HashMap<>();

    static {
        oreToTeam.put(Material.DIAMOND_ORE, NamedTextColor.BLUE);
        oreToTeam.put(Material.IRON_ORE, NamedTextColor.GRAY);
        oreToTeam.put(Material.COAL_ORE, NamedTextColor.BLACK);
        oreToTeam.put(Material.GOLD_ORE, NamedTextColor.YELLOW);
        oreToTeam.put(Material.COPPER_ORE, NamedTextColor.GOLD);
        oreToTeam.put(Material.EMERALD_ORE, NamedTextColor.GREEN);
        oreToTeam.put(Material.LAPIS_ORE, NamedTextColor.DARK_BLUE);
        oreToTeam.put(Material.REDSTONE_ORE, NamedTextColor.DARK_RED);

        HashMap<Material, NamedTextColor> _oretoTeam = new HashMap<>();
        for(Material material : oreToTeam.keySet()){
            _oretoTeam.put(Material.valueOf("DEEPSLATE_" + material.name()), oreToTeam.get(material));
        }
        oreToTeam.putAll(_oretoTeam);

        oreToTeam.put(Material.NETHER_QUARTZ_ORE, NamedTextColor.WHITE);
        oreToTeam.put(Material.NETHER_GOLD_ORE, NamedTextColor.YELLOW);

        for(NamedTextColor color : oreToTeam.values()){
            Team cTeam = Hardcore.instance.mainScoreboard.getTeam(color.asHSV().toString());
            if(cTeam == null)
                cTeam = Hardcore.instance.mainScoreboard.registerNewTeam(color.asHSV().toString());
            cTeam.color(color);
        }
    }

    public static List<Block> getBlocksInRadius(Block start, int radius){
        List<Block> blockList = new ArrayList<>();
        for(int x = start.getLocation().getBlockX() - radius; x <= start.getLocation().getBlockX() + radius; x++){
            for(int y = start.getLocation().getBlockY() - radius; y <= start.getLocation().getBlockY() + radius; y++){
                for(int z = start.getLocation().getBlockZ() - radius; z <= start.getLocation().getBlockZ() + radius; z++){
                    blockList.add(new Location(start.getWorld(), x, y, z).getBlock());
                }
            }
        }
        return blockList;
    }

    private static List<Block> filterBlocks(List<Block> blockList){
        return blockList.stream().filter((b) -> oreToTeam.containsKey(b.getType())).toList();
    }

    public static void giveTempXrayToPlayer(Player player, int time, int radius){
        List<Block> blockList = getBlocksInRadius(player.getLocation().getBlock(), radius);
        List<Block> filteredList = filterBlocks(blockList);

        List<Slime> spawnedSlimes = new ArrayList<>();

        for(Block block : filteredList) {
            Slime slime = (Slime) player.getWorld().spawnEntity(block.getLocation().add(0.5, 0.25, 0.5), EntityType.SLIME);
            slime.setGlowing(true);
            slime.setAI(false);
            slime.setSize(1);
            slime.setInvisible(true);
            slime.setInvulnerable(true);
            Hardcore.instance.mainScoreboard.getTeam(oreToTeam.get(block.getType()).asHSV().toString()).addEntity(slime);
            spawnedSlimes.add(slime);
            slimeStorage.put(block, slime);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () ->{
            for(Slime slime : spawnedSlimes){
                slime.remove();
            }
            for(Block block : filteredList){
                slimeStorage.remove(block);
            }
        }, time);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if(slimeStorage.containsKey(block)){
            slimeStorage.remove(block).remove();
        }
    }
}
