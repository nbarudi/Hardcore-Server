package ca.bungo.hardcore.modules.item.weapons;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.key.Key;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class NyanGun extends ItemModule implements CraftableModule {


    private final net.kyori.adventure.sound.Sound sound = net.kyori.adventure.sound.Sound.sound(Key.key("custom.nyangun"),
            net.kyori.adventure.sound.Sound.Source.PLAYER, 0.5f, 1);

    public NyanGun(String moduleName) {
        super(moduleName);
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("nyanGun");
        this.castingKey = "nyan-gun";
        this.shouldDebounce = false;
        this.cost = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
        this.costAmount = 1;
        this.costMessage = "&eCats need energy to fight with you! This one demands at least ".convertToComponent()
                .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
        this.hasCost = true;
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();

        RayTraceResult traceResult = player.rayTraceEntities(50);

        if(traceResult != null){
            Entity result = traceResult.getHitEntity();
            if(result != null && !result.equals(player)){
                if(result instanceof LivingEntity){
                    ((CraftLivingEntity) result).getHandle().hurt(((CraftLivingEntity) result).
                            getHandle().damageSources().fireworks(null, null), 3);
                    ((CraftLivingEntity) result).getHandle().invulnerableTime = 0;
                    ((CraftLivingEntity) result).getHandle().lastHurtByPlayer = ((CraftPlayer) player).getHandle();
                }
            }
        }

        ParticleBuilder particleBuilder = new ParticleBuilder(Particle.REDSTONE);
        particleBuilder.count(10);
        particleBuilder.allPlayers();
        int r = new Random().nextInt(0, 255);
        int g = new Random().nextInt(0, 255);
        int b = new Random().nextInt(0, 255);
        particleBuilder.color(r, g, b);
        Location startLocation = player.getEyeLocation().clone();
        Vector dir = startLocation.getDirection();
        Location dummyLocation = startLocation.clone();
        int count = 0;
        while(count < 50){
            count++;
            particleBuilder.location(dummyLocation);
            particleBuilder.spawn();
            dummyLocation = dummyLocation.add(dir);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 2f ,1f);
    }

    @EventHandler
    public void onEquipChange(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        int prevSlot = event.getPreviousSlot();

        ItemStack newItem = player.getInventory().getItem(newSlot);
        ItemStack prevItem = player.getInventory().getItem(prevSlot);

        if(prevItem == null || verifyItem(prevItem)){
            player.stopSound(sound);
        }
        if(newItem != null && verifyItem(newItem)){
            player.playSound(sound);
        }
    }

    @Override
    public Recipe getItemRecipe() {
        return null;
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "AdvancedMagicWeapons";
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.castingKey);
    }
}
