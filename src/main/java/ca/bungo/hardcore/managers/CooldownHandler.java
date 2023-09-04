package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.types.Cooldown;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CooldownHandler {

    private final Map<String, Cooldown> cooldownList;
    private final HardcorePlayer hardcorePlayer;
    public CooldownHandler(HardcorePlayer player){
        this.cooldownList = new HashMap<>();
        this.hardcorePlayer = player;
    }

    public void createCooldown(String name, int seconds, @Nullable ItemStack cooldownItem){
        Cooldown cooldown = new Cooldown(seconds*20);
        this.cooldownList.put(name, cooldown);

        if(cooldownItem != null){
            hardcorePlayer.getBukkitPlayer().setCooldown(cooldownItem.getType(), seconds*20);
            cooldown.setAssocItem(cooldownItem);
        }
    }

    public boolean containsCooldown(String name){
        return this.cooldownList.containsKey(name);
    }

    public boolean onCooldown(String name){
        Cooldown cooldown = cooldownList.get(name);
        if(cooldown == null) return false;
        if(cooldown.isCompleted()){
            cooldownList.remove(name);
            return false;
        }
        return true;
    }

    public double getCooldownRemaining(String name){
        Cooldown cooldown = this.cooldownList.get(name);
        int ticks = cooldown.getTicks();
        return ticks/20.0;
    }

    public Cooldown getCooldown(String name){
        return this.cooldownList.get(name);
    }

    public Map<String, Cooldown> getCooldownList() { return this.cooldownList; }

}
