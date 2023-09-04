package ca.bungo.hardcore.features.systems.events;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.block.custom.PlayerModTableBlock;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SkillShopFeature implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        Inventory clicked = event.getInventory();

        Inventory skillInventory = PlayerModTableBlock.playerSkillShops.get(player.getUniqueId().toString());
        if(skillInventory == null) return;
        if(clicked.equals(skillInventory)){
            event.setCancelled(true);

            ItemStack itemStack = event.getCurrentItem();
            if(itemStack == null) return;

            String moduleName = itemStack.getItemMeta().getPersistentDataContainer().get(Hardcore.instance.customItemManager.getItemKey(),
                    PersistentDataType.STRING);

            Module module = Hardcore.instance.moduleManager.getModuleByName(moduleName);
            if(module == null) return;

            BuyableModule buyableModule = (BuyableModule) module;

            if(hardcorePlayer.hasModule(buyableModule.getModuleName())){
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                player.sendMessage(("&4You already have this skill!").convertToComponent());
                return;
            }

            if(!hardcorePlayer.purchase(buyableModule.getCost(), module)){
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                player.sendMessage(("&4You do not have enough points for this skill!").convertToComponent());
                return;
            }

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
            player.closeInventory();
            player.sendMessage(("&aYou have purchased &e" + buyableModule.friendlyName()).convertToComponent());
        }
    }

}
