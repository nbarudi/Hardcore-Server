package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.features.bosses.types.BardgoBoss;
import ca.bungo.hardcore.features.bosses.types.StrangeGolemBoss;
import ca.bungo.hardcore.features.bosses.types.SuperCreeperBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class BossManager {

    private final Map<String, Boss> bossMap;

    public BossManager(){
        bossMap = new HashMap<>();

        loadBosses();
    }

    private void loadBosses(){

        bossMap.put("SuperCreeperBoss", new SuperCreeperBoss("SuperCreeperBoss"));
        bossMap.put("StrangeGolemBoss", new StrangeGolemBoss("StrangeGolemBoss"));
        bossMap.put("BardgoBoss", new BardgoBoss("BardgoBoss"));

        for(Boss boss : bossMap.values()){
            Bukkit.getPluginManager().registerEvents(boss, Hardcore.instance);
        }
    }

    public boolean spawnBoss(String name, Location location){
        if(!bossMap.containsKey(name)) return false;
        return bossMap.get(name).spawnSelf(null, location);
    }

    public Map<String, Boss> getBossMap() { return this.bossMap; }




}
