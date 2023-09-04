package extensions.org.bukkit.entity.Player;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.managers.PlayerManager;
import ca.bungo.hardcore.types.HardcorePlayer;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Extension
public class PlayerExtension {


    public static HardcorePlayer getHardcorePlayer(@This Player player){
        return PlayerManager.playerList.get(player.getUniqueId().toString());
    }

    public static void registerHardcorePlayer(@This Player player){
        HardcorePlayer hardcorePlayer = getHardcorePlayer(player);
        if(hardcorePlayer != null){
            hardcorePlayer.updatePlayer(player);
        }else{
            FileConfiguration configuration = Hardcore.instance.getConfig();
            if(configuration.getConfigurationSection(player.getUniqueId().toString()) == null || configuration.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).size() <= 1){
                hardcorePlayer = new HardcorePlayer(player);
            }else{
                hardcorePlayer = (HardcorePlayer) configuration.get(player.getUniqueId() + ".hardcore-module");
            }
            assert hardcorePlayer != null;
            Bukkit.getServer().getPluginManager().registerEvents(hardcorePlayer, Hardcore.instance);
        }
        PlayerManager.playerList.put(player.getUniqueId().toString(), hardcorePlayer);
    }

    public static void unload() {

    }

}
