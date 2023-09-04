package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.items.events.LightningEvents;
import ca.bungo.hardcore.features.items.events.WorldGenEvents;
import ca.bungo.hardcore.features.systems.events.ExperienceGainEvents;
import ca.bungo.hardcore.features.systems.events.SkillShopFeature;
import org.bukkit.Bukkit;

public class FeatureManager {

    public FeatureManager() {
        registerFeatureEvents();
    }

    private void registerFeatureEvents(){
        Bukkit.getPluginManager().registerEvents(new LightningEvents(), Hardcore.instance);
        Bukkit.getPluginManager().registerEvents(new WorldGenEvents(), Hardcore.instance);
        Bukkit.getPluginManager().registerEvents(new ExperienceGainEvents(), Hardcore.instance);
        Bukkit.getPluginManager().registerEvents(new SkillShopFeature(), Hardcore.instance);
    }

}
