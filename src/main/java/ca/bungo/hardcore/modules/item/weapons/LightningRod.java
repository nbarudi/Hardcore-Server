package ca.bungo.hardcore.modules.item.weapons;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class LightningRod extends ItemModule implements CraftableModule {

    public LightningRod(String moduleName) {
        super(moduleName);
        this.castingKey = "lightning-rod";
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("lightningRod");
        this.hasCooldown = true;
        this.hasCost = true;
        this.cost = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
        this.costAmount = 15;
        this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        int maxDistance = 20;
        Player player = event.getPlayer();
        RayTraceResult rayTraceBlock = player.rayTraceBlocks(maxDistance);
        RayTraceResult rayTraceEntity = player.rayTraceEntities(maxDistance);
        Vector direction = player.getLocation().getDirection();
        Location eyeLocation = player.getEyeLocation().clone();
        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.color(Color.YELLOW);
        builder.count(15);
        builder.allPlayers();
        builder.offset(0.3, 0, 0);
        builder.source(player);

        for(int i = 0; i < maxDistance*10; i++){
            eyeLocation = eyeLocation.add(direction);
            if(eyeLocation.distance(player.getEyeLocation()) > maxDistance)
                break;
            builder.location(eyeLocation);
            builder.spawn();
        }
        if(rayTraceEntity != null){
            Entity hit = rayTraceEntity.getHitEntity();
            if(hit == null) return;
            player.getWorld().strikeLightning(hit.getLocation());
        }
        else if(rayTraceBlock != null){
            Block block = rayTraceBlock.getHitBlock();
            if(block == null) return;
            player.getWorld().strikeLightning(block.getLocation());
        }else{
            player.getWorld().strikeLightning(eyeLocation);
        }
        player.sendMessage(("&eLightning has been summoned!").convertToComponent());
        player.getHardcorePlayer().addCooldown(this.getModuleName(), 5, this.castingItem);
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.getCastingKey()), this.castingItem);
        recipe.shape("BCB",
                     "CRC",
                     "BCB");
        recipe.setIngredient('B', Hardcore.instance.customItemManager.getCustomItem("lightningInABottle"));
        recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('R', Material.LIGHTNING_ROD);
        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.getCastingKey());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)){
            if(event.getEntity() instanceof Creeper){
                Random random = new Random();
                int chance = random.nextInt(0, 1000);
                if(chance <= 10){
                    Location location = event.getEntity().getLocation();
                    if(Hardcore.instance.bossManager.spawnBoss("SuperCreeperBoss", location)){
                        event.getEntity().remove();
                        Bukkit.broadcast((String.format("&cA Boss has been spawned at: %.2f, %.2f, %.2f",
                                location.getX(), location.getY(), location.getZ())).convertToComponent());
                    }
                }
            }
        }
    }

    @Override
    public boolean requiresModuleToCreate() { return true; }

    @Override
    public String overrideModuleName() { return "AdvancedMagicWeapons"; }
}
