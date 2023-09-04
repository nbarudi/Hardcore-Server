package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    public static final Map<String, HardcorePlayer> playerList = new HashMap<>();

    public void unload(){
        for(HardcorePlayer player : playerList.values()){
            player.saveConfig();
        }
    }

}
