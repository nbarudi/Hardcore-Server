package ca.bungo.hardcore.modules.item.utilityItems;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Random;

public class SpectralShears extends ItemModule implements CraftableModule {

    Random random = new Random();

    public SpectralShears(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("spectralShears");
        this.castingKey = "spectral-shears";
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {}

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(this.castingItem == null) return;
        if(debounce) return;
        if(this.verifyItem(player.getInventory().getItemInMainHand()) || this.verifyItem(player.getInventory().getItemInOffHand()))
            if(this.canRun(player)){
                Entity entity = event.getRightClicked();
                if(entity instanceof HumanEntity) return;
                if(!(entity instanceof LivingEntity livingEntity)) return;
                if(!(livingEntity instanceof Animals)) return;
                if(livingEntity.isDead()) return;
                if(livingEntity.isInvulnerable()) return;


                livingEntity.damage(6);
                livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(),
                        Hardcore.instance.customItemManager.getCustomItem("soulShard"));
                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 0.5f);
                player.sendMessage("&eSnip Snip...".convertToComponent());

                //ToDo: Enraged Mob Boss


                int chance = random.nextInt(100);
                if(chance > 80){
                    if(Hardcore.instance.bossManager.spawnTypedBoss("EnragedMobBoss", livingEntity.getLocation(), livingEntity.getType()))
                        livingEntity.remove();
                }
            }
        if(this.shouldDebounce){
            debounce = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () ->{
                debounce = false;
            }, 5);
        }
    }

    @Override
    public Recipe getItemRecipe() {
        return new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem)
                .shape("BLB", "ASA", "LBL")
                .setIngredient('L', Hardcore.instance.customItemManager.getCustomItem("lowCovalDust"))
                .setIngredient('A', Material.LAPIS_LAZULI)
                .setIngredient('B', Material.SNOWBALL)
                .setIngredient('S', Material.SHEARS);
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "SimpleMagicTools";
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.castingKey);
    }
}
