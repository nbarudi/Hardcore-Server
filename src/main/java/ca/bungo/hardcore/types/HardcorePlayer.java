package ca.bungo.hardcore.types;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.managers.CooldownHandler;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.events.custom.PlayerLevelDownEvent;
import ca.bungo.hardcore.events.custom.PlayerLevelUpEvent;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SerializableAs("HardcorePlayer")
public class HardcorePlayer implements Listener, ConfigurationSerializable {

    private Player player;
    private final String uuid;

    private final CooldownHandler cooldownHandler;

    private final Map<String, Module> knownModules;

    private int level;
    private double experience;
    private int points;


    public HardcorePlayer(Player player){
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        this.knownModules = new HashMap<>();
        this.cooldownHandler = new CooldownHandler(this);
        this.level = 1;
        this.experience = 0;
        this.points = 0;

        saveConfig();
    }

    public HardcorePlayer(Map<String, Object> des){
        this.uuid = (String) des.get("owning-uuid");
        this.level = (int)des.get("level");
        this.experience = (double)des.get("experience");
        this.points = (int)des.get("points");
        this.player = Bukkit.getPlayer(UUID.fromString(this.uuid));
        cooldownHandler = new CooldownHandler(this);
        this.knownModules = new HashMap<>();

        List<String> knownMods = (List<String>) des.get("known-modules");
        for(String name : knownMods){
            if(Hardcore.instance.moduleManager == null){
                Bukkit.getLogger().warning("Strange Case: ModuleManager is Null!");
                continue;
            }
            this.knownModules.put(name, Hardcore.instance.moduleManager.getModuleByName(name));
        }
    }

    public Player getBukkitPlayer(){
        return this.player;
    }
    public void updatePlayer(Player player){
        this.player = player;
    }
    public void addLevel(int amount){
        this.setLevel(this.level + amount);
    }
    public void removeLevel(int amount){ this.setLevel(this.level - amount); }
    public void setLevel(int level){
        if(level < this.level){
            PlayerLevelDownEvent event = new PlayerLevelDownEvent(this, this.level, level);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () -> Bukkit.getPluginManager().callEvent(event));
            if(event.isCancelled()) return;
        }else{
            this.points += level - this.level;
            PlayerLevelUpEvent event = new PlayerLevelUpEvent(this, this.level, level);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () -> Bukkit.getPluginManager().callEvent(event));
            if(event.isCancelled()) return;
        }
        this.level = level;

        saveConfig();
    }
    public int getLevel(){ return this.level; }

    public double getExperience() { return this.experience; }
    public void addExperience(double amount) {
        this.experience += amount;

        if(this.experience >= (1000 + this.level*500)){
            this.addLevel(1);
            this.setExperience(this.experience - (1000 + (this.level-1)*500));
        }

        saveConfig();
    }
    public void setExperience(double experience) {
        this.experience = experience;

        if(this.experience >= (1000 + this.level*500)){
            this.setExperience(this.experience - (1000 + (this.level)*500));
            this.addLevel(1);
        }
        else if(this.experience < 0){
            this.removeLevel(1);
            this.setExperience((1000 + (this.level)*500) + this.experience);
        }

        saveConfig();
    }
    public void removeExperience(int amount) {
        this.experience -= amount;

        if(this.experience < 0){
            this.removeLevel(1);
            this.setExperience((1000 + this.level*500) + this.experience);
        }

        saveConfig();
    }

    public boolean hasPoints(int cost){
        return this.points >= cost;
    }

    public boolean purchase(int cost, Module module){
        BuyableModule buyableModule = (BuyableModule) module;
        if(!this.hasModule(buyableModule.depends()))
            return false;
        if(!this.hasPoints(cost))
            return false;
        this.points -= cost;
        this.addModule(module);
        saveConfig();
        return true;
    }

    public void addPoints(int amount){
        this.points += amount;
    }

    public void removePoints(int amount){
        this.points -= amount;
    }

    public void setPoints(int amount) {
        this.points = amount;
    }

    public int getPoints() {
        return this.points;
    }


    public void addCooldown(String name, int seconds, @Nullable ItemStack cooldownItem){
        cooldownHandler.createCooldown(name, seconds, cooldownItem);
    }

    public boolean onCooldown(String name){
        return cooldownHandler.onCooldown(name);
    }

    public boolean containsCooldown(String name){
        return cooldownHandler.containsCooldown(name);
    }


    public double getRemainingCooldown(String name){
        return cooldownHandler.getCooldownRemaining(name);
    }

    public Component getCooldownComponent(String name){
        Cooldown cooldown = cooldownHandler.getCooldown(name);
        if(cooldown == null)
            return null;
        return cooldown.getCooldownMessage();
    }

    public boolean hasModule(String name){
        if(name == null || name.isEmpty()) return true;
        return this.knownModules.containsKey(name);
    }

    public void addModule(Module module){
        this.knownModules.put(module.getModuleName(), module);
        saveConfig();
    }

    public Map<String, Module> getKnownModules() {
        return this.knownModules;
    }

    public void saveConfig(){
        FileConfiguration fileConfiguration = Hardcore.instance.getConfig();
        ConfigurationSection config = fileConfiguration.getConfigurationSection(uuid);
        player = Bukkit.getPlayer(UUID.fromString(this.uuid));
        if(config == null)
            config = fileConfiguration.createSection(uuid);
        if(player != null && player.isOnline())
            config.set("username", player.getName());
        config.set("hardcore-module", this);

        Hardcore.instance.saveConfig();
    }


    //Events
    @EventHandler
    public void onServerTick(ServerTickStartEvent event){
        if(player == null || !player.isOnline()) return;

        ItemStack heldItem = player.getInventory().getItemInMainHand().clone();
        heldItem.setAmount(1);

        if(heldItem.getItemMeta() == null) return;
        if(!heldItem.getItemMeta().getPersistentDataContainer().has(Hardcore.instance.customItemManager.getItemKey(), PersistentDataType.STRING)) return;

        Optional<Cooldown> _cooldown = cooldownHandler.getCooldownList().values().stream().filter(
                (c) -> c.getAssocItem() != null
                        && c.getAssocItem().getItemMeta() != null
                        && c.getAssocItem().getItemMeta().getPersistentDataContainer().has(Hardcore.instance.customItemManager.getItemKey(), PersistentDataType.STRING)
                        && c.getAssocItem().getItemMeta().getPersistentDataContainer().get(Hardcore.instance.customItemManager.getItemKey(), PersistentDataType.STRING)
                        .equals(heldItem.getItemMeta().getPersistentDataContainer().get(Hardcore.instance.customItemManager.getItemKey(), PersistentDataType.STRING))
        ).findFirst();

        if(_cooldown.isPresent()){
            Cooldown cooldown = _cooldown.get();
            if(cooldown.isCompleted()) return;
            if(cooldown.getTicks() <= 0){
                player.sendActionBar(("&aCooldown Finished!").convertToComponent());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1f);
            }
            else{
                player.sendActionBar(cooldown.getCooldownMessage());
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("&#753012Name: &e").append(this.player.getName()).append("\n");
        builder.append("&#753012UUID: &e").append(this.uuid).append("\n");
        builder.append("&#753012Level: &e").append(this.level).append("\n");
        builder.append("&#753012Current Experience: &e").append(this.experience).append("\n");
        builder.append("&#753012Experience For Level-Up: &e").append(1000 + (level*500)).append("\n");
        builder.append("&#753012Unspent Points: &e").append(this.points).append("\n");
        builder.append("&#753012Module List:\n");
        Set<String> modules = knownModules.keySet();
        List<String> moduleNames = modules.stream().sorted((a, b) -> {
            Module moduleA = Hardcore.instance.moduleManager.getModuleByName(a);
            Module moduleB = Hardcore.instance.moduleManager.getModuleByName(b);
            if(moduleA instanceof BuyableModule buyableModule){
                if(buyableModule.depends() == null) return 1;
                if(buyableModule.depends().equalsIgnoreCase(moduleB.getModuleName()))
                    return -1;
                else if(moduleB instanceof BuyableModule buyableModule1){
                    if(buyableModule1.depends() == null) return -1;
                    if(buyableModule1.depends().equalsIgnoreCase(buyableModule.depends()))
                        return 0;
                }else{
                    return 1;
                }
            }
            return -1;
        }).toList();
        for(String _module : moduleNames){
            Module module = Hardcore.instance.moduleManager.getModuleByName(_module);
            builder.append("&e- ").append(module.friendlyName()).append("\n");
        }

        return builder.toString();
    }

    //Config Helper
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> fieldDetails = new LinkedHashMap<>();

        fieldDetails.put("owning-uuid", this.uuid);
        fieldDetails.put("known-modules", this.knownModules.keySet().stream().toList());
        fieldDetails.put("level", this.level);
        fieldDetails.put("experience", this.experience);
        fieldDetails.put("points", this.points);

        return fieldDetails;
    }
}
