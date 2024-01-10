package ca.bungo.hardcore.features.systems.events;

import ca.bungo.hardcore.Hardcore;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BlockFixer implements Listener {

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event){
        Hardcore.instance.moduleManager.loadBlocksInChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event){
        Hardcore.instance.moduleManager.unloadBlocksInChunk(event.getChunk());
    }

//
//    @EventHandler
//    public void onChunkUnload(ChunkUnloadEvent event){
//        Hardcore.instance.moduleManager.unloadBlocksInChunk(event.getChunk());
//    }

}
