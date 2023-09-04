package ca.bungo.hardcore.modules.item.weapons;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public class HandCannon extends ItemModule implements CraftableModule {

    public HandCannon(String moduleName) {
        super(moduleName);
        this.hasCooldown = true;
        this.hasCost = true;
        this.cost = new ItemStack(Material.TNT);
        this.castingKey = "hand-cannon";
        this.castingItem = Hardcore.instance.customItemManager.getCustomItem("handCannon");
        this.costMessage = ("&cYou are missing required items for this cast! Required Items: &e" + this.costAmount + "x ").convertToComponent()
                .append(cost.displayName().hoverEvent(cost.asHoverEvent()));
    }

    @Override
    protected void runItemAbility(PlayerInteractEvent event) {
        HardcorePlayer hardcorePlayer = event.getPlayer().getHardcorePlayer();
        hardcorePlayer.addCooldown(this.getModuleName(), 1, null);

        int maxDistance = 30;
        Player player = event.getPlayer();
        RayTraceResult rayTraceBlock = player.rayTraceBlocks(maxDistance);
        RayTraceResult rayTraceEntity = player.rayTraceEntities(maxDistance);
        Vector direction = player.getLocation().getDirection();
        Location eyeLocation = player.getEyeLocation().clone();
        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.color(Color.BLACK);
        builder.count(50);
        builder.allPlayers();
        builder.offset(0.5, 0.5, 0.5);
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
            player.getWorld().createExplosion(hit.getLocation(), 3f, true, false);
        }
        else if(rayTraceBlock != null){
            Block block = rayTraceBlock.getHitBlock();
            if(block == null) return;
            player.getWorld().createExplosion(block.getLocation(), 3f, true, false);
        }else{
            player.getWorld().createExplosion(eyeLocation, 3f, true, false);
        }

        player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(-2)));
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.castingKey), this.castingItem);

        RecipeChoice.MaterialChoice choice = new RecipeChoice.MaterialChoice(Tag.LOGS);


        recipe.shape(
                "MMI",
                "WCI",
                "MMI");
        recipe.setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("chargedHeart"));
        recipe.setIngredient('W', choice);

        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.castingKey);
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "AdvancedMagicWeapons";
    }
}
