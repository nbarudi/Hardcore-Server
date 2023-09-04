package ca.bungo.hardcore.utility;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtility {

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static void squareParticleZone(Location positionA, Location positionB, ParticleBuilder builder){
        List<Block> blocks = blocksFromTwoPoints(positionA, positionB);
        List<Block> filtered = new ArrayList<>();

        int xA = positionA.getBlockX();
        int zA = positionA.getBlockZ();
        int xB = positionB.getBlockX();
        int zB = positionB.getBlockZ();

        for(Block _block : blocks){
            if(_block.getLocation().getBlockX() == xA || _block.getLocation().getBlockX() == xB)
                filtered.add(_block);
            else if(_block.getLocation().getBlockZ() == zA || _block.getLocation().getBlockZ() == zB)
                filtered.add(_block);
        }
        for(Block block : filtered){
            builder.location(block.getLocation().add(0.5, 0 , 0.5));
            builder.spawn();
        }

    }

}
