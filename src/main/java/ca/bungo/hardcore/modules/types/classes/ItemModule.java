package ca.bungo.hardcore.modules.types.classes;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;


public abstract class ItemModule extends Module implements Listener {

    protected ItemStack castingItem;
    protected String castingKey;

    private boolean debounce = false;


    public ItemModule(String moduleName) {
        super(moduleName);
    }

    public String getCastingKey(){
        return this.castingKey;
    }

    protected boolean verifyItem(@NotNull ItemStack toCompare) {
        this.verifyPersist();
        if(toCompare.getItemMeta() == null) return false;
        PersistentDataContainer container = toCompare.getItemMeta().getPersistentDataContainer();
        if(!container.has(this.itemKey)) return false;
        String containerString = container.get(this.itemKey, PersistentDataType.STRING);
        if(containerString == null) return false;
        return containerString.equals(castingKey);
    }


    private void verifyPersist(){
        ItemMeta meta = castingItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(!container.has(this.itemKey, PersistentDataType.STRING)){
            container.set(this.itemKey, PersistentDataType.STRING, "castingItem");
            castingItem.setItemMeta(meta);
        }
    }

    protected abstract void runItemAbility(PlayerInteractEvent event);

    protected boolean canRun(Player castingPlayer){
        return this.passesCooldown(castingPlayer) && this.passesCost(castingPlayer);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(this.castingItem == null) return;
        if(debounce) return;
        if(this.verifyItem(player.getInventory().getItemInMainHand()) || this.verifyItem(player.getInventory().getItemInOffHand()))
            if(this.canRun(player))
                this.runItemAbility(event);
        debounce = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () ->{
            debounce = false;
        }, 5);
    }

    public ItemStack getCastingItem(){
        return this.castingItem;
    }

}
