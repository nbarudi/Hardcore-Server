package ca.bungo.hardcore.modules;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.types.HardcorePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class Module {

    private final String moduleName;
    protected final NamespacedKey itemKey;
    protected boolean hasCooldown = false;
    protected boolean hasCost = false;

    protected ItemStack cost;
    protected int costAmount = 1;

    protected Component costMessage;

    public Module(String moduleName){
        this.moduleName = moduleName;
        this.itemKey = Hardcore.instance.customItemManager.getItemKey();
    }


    public String getModuleName() {
        return moduleName;
    }

    protected boolean passesCooldown(Player player){
        if(!this.hasCooldown) return true;
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        if(hardcorePlayer.onCooldown(this.getModuleName())){
            player.sendMessage(hardcorePlayer.getCooldownComponent(this.getModuleName()));
            return false;
        }
        return true;
    }

    protected boolean passesCost(Player player){
        if(!this.hasCost || this.cost == null) return true;
        PlayerInventory inventory = player.getInventory();
        if(inventory.containsAtLeast(cost, costAmount)){
            ItemStack costItem = cost.clone();
            costItem.setAmount(this.costAmount);
            inventory.removeItemAnySlot(costItem);
            return true;
        }
        if(this.costMessage != null)
            player.sendMessage(this.costMessage);
        return false;
    }

    public boolean isCooldown() {
        return hasCooldown;
    }

    public boolean isCost() {
        return hasCost;
    }

    public String friendlyName(){
        return this.getModuleName();
    }

}
