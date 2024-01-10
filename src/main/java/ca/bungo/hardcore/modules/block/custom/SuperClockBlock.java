package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.modules.utility.XRayUtility;
import ca.bungo.hardcore.utility.InventoryUtility;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class SuperClockBlock extends CustomBlockModule implements CraftableModule {

    private final List<String> enabledWatches;

    private final Map<String, Inventory> internalInventory;


    public SuperClockBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("superWatch"));
        this.blockKey = "superclock-block";
        this.enabledWatches = new ArrayList<>();
        this.internalInventory = new HashMap<>();
        tickTimer();
    }


    @Override
    protected void onPlace(Interaction interaction, ItemDisplay display) {
        super.onPlace(interaction, display);
        internalInventory.put(interaction.getUniqueId().toString(), Bukkit.createInventory(null, 9, Component.text("Fuel", NamedTextColor.DARK_RED)));
    }

    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        if(event.getPlayer().isSneaking()){
            event.getPlayer().openInventory(this.internalInventory.get(this.playerClicks.get(event.getPlayer().getUniqueId().toString())));
        }else{
            toggleTime(event.getPlayer());
        }
    }


    @Override
    protected Map<String, Object> saveCustomData(Interaction interaction) {
        Map<String, Object> customData = new HashMap<>();
        customData.put("toggled", this.enabledWatches.contains(interaction.getUniqueId().toString()));
        customData.put("inventory-contents", InventoryUtility.convertInventory(internalInventory.get(interaction.getUniqueId().toString())));
        return customData;
    }

    @Override
    protected void loadCustomData(ConfigurationSection section, Interaction interaction) {
        if(section.getBoolean("toggled"))
            enabledWatches.add(interaction.getUniqueId().toString());
        String iB64 = section.getString("inventory-contents");
        this.internalInventory.get(interaction.getUniqueId().toString()).setContents(InventoryUtility.getSavedInventory(iB64));
    }

    private void toggleTime(Player player){
        Interaction interaction = (Interaction) player.getWorld().getEntity(UUID.fromString(this.playerClicks.get(player.getUniqueId().toString())));
        if(interaction == null) return;
        if(!this.internalInventory.get(interaction.getUniqueId().toString()).containsAtLeast(Hardcore.instance.customItemManager.getCustomItem("fuelItem"), 1)){
            interaction.getWorld().playSound(interaction.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.5f);
            player.sendMessage(Component.text("No Fuel!", NamedTextColor.DARK_RED));
            return;
        }
        if(enabledWatches.contains(interaction.getUniqueId().toString())){
            enabledWatches.remove(interaction.getUniqueId().toString());
            ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
            builder.color(Color.BLACK);
            builder.location(interaction.getLocation().add(0,1.5,0));
            builder.offset(0.25,0.25,0.25);
            builder.allPlayers();
            builder.count(25);
            builder.spawn();
            interaction.getWorld().playSound(interaction.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.5f);
        }else{
            enabledWatches.add(interaction.getUniqueId().toString());
            ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
            builder.color(Color.LIME);
            builder.location(interaction.getLocation().add(0,1.5,0));
            builder.offset(0.25,0.25,0.25);
            builder.allPlayers();
            builder.count(25);
            builder.spawn();
            interaction.getWorld().playSound(interaction.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 1f);
        }
    }

    private void tickNearBlocks(Interaction interaction){
        Location location = interaction.getLocation();
        int radius = 4;

        List<Block> blockList = XRayUtility.getBlocksInRadius(location.getBlock(), radius);

        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.color(Color.SILVER);
        builder.offset(0,0.1,0);
        builder.allPlayers();
        builder.count(1);

        for(Block block : blockList){
            if(block.getBlockData().isRandomlyTicked()){
                block.randomTick();
                builder.location(block.getLocation().add(0.5, 1.5, 0.5));
                builder.spawn();
            }
            else if(block.getState() instanceof Furnace furnace){
                furnace.setCookTime((short)(furnace.getCookTime() + 20));
                furnace.update();
                builder.location(block.getLocation().add(0.5, 1.5, 0.5));
                builder.spawn();
            }
        }
    }

    private void tickUseFuel(Interaction interaction){
        Inventory internal = this.internalInventory.get(interaction.getUniqueId().toString());
        ItemStack itemStack = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
        if(!internal.containsAtLeast(itemStack, 1)){
            ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
            builder.color(Color.BLACK);
            builder.location(interaction.getLocation().add(0,1.5,0));
            builder.offset(0.25,0.25,0.25);
            builder.allPlayers();
            builder.count(25);
            builder.spawn();
            interaction.getWorld().playSound(interaction.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.5f);
            enabledWatches.remove(interaction.getUniqueId().toString());
        }
        else {
            internal.removeItemAnySlot(itemStack);
        }
    }

    @Override
    protected void broken(Interaction interaction, ItemDisplay display) {
        if(this.enabledWatches.contains(interaction.getUniqueId().toString()))
            enabledWatches.remove(interaction.getUniqueId().toString());
        Inventory inventory = internalInventory.remove(interaction.getUniqueId().toString());
        ItemStack[] contents = inventory.getContents();
        for(ItemStack item : contents){
            if(item == null) continue;
            interaction.getWorld().dropItem(interaction.getLocation(), item);
        }
    }

    private void tickTimer(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, ()->{
            for(String interaction : enabledWatches){
                for(World world : Bukkit.getWorlds()){
                    if(world.getEntity(UUID.fromString(interaction)) == null) continue;
                    tickNearBlocks((Interaction) world.getEntity(UUID.fromString(interaction)));
                    break;
                }
            }
        }, 2, 2);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, ()->{

            for(String interaction : enabledWatches){
                for(World world : Bukkit.getWorlds()){
                    if(world.getEntity(UUID.fromString(interaction)) == null) continue;
                    Bukkit.getScheduler().runTaskLater(Hardcore.instance, () ->
                            tickUseFuel((Interaction) world.getEntity(UUID.fromString(interaction))), 0);
                    break;
                }

            }
        }, 20, 20);
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.blockKey), this.blockItem);
        recipe.shape(
                "HNH",
                "CEC",
                "HNH");
        recipe.setIngredient('H', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('C', Hardcore.instance.customItemManager.getCustomItem("chargedHeart"));
        recipe.setIngredient('E', Material.DRAGON_EGG);
        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.blockKey);
    }
}
