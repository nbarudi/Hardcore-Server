package ca.bungo.hardcore.modules.item.recipeItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ChargedHeart extends ItemModule {


    public ChargedHeart(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("chargedHeart");
        this.castingKey = "charged-heart";
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {}

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().clone().asOne();
        if(!itemStack.equals(Hardcore.instance.customItemManager.getCustomItem("chargedHeart"))) return;
        player.getWorld().createExplosion(player.getLocation(), 20f, false, false);
        player.sendMessage(("&4Now what did you think was gonna happen?").convertToComponent());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Entity entity = event.getRightClicked();

        if(!entity.getType().equals(EntityType.IRON_GOLEM) || !verifyItem(itemStack)) return;

        if(Hardcore.instance.bossManager.spawnBoss("StrangeGolemBoss", entity.getLocation())){
            entity.remove();
            entity.getWorld().createExplosion(entity.getLocation(), 2f, false);
            player.getInventory().removeItemAnySlot(this.castingItem);
        }
    }

}
