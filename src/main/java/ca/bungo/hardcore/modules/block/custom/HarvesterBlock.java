package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.InventoryUtility;
import ca.bungo.hardcore.utility.ParticleUtility;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
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

public class HarvesterBlock extends CustomBlockModule implements CraftableModule {

    private final List<Interaction> enabledHarvesters;
    private final HashMap<Interaction, Inventory> internalInventory;
    public HarvesterBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("harvester"));
        this.enabledHarvesters = new ArrayList<>();
        this.internalInventory = new HashMap<>();
        this.blockKey = "harvester-block";
        tickTimer();
    }

    @Override
    protected void onPlace(Interaction interaction, ItemDisplay display) {
        super.onPlace(interaction, display);
        internalInventory.put(interaction, Bukkit.createInventory(null, 36, Component.text("Harvester", NamedTextColor.YELLOW)));
    }

    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        if(event.getPlayer().isSneaking()){
            event.getPlayer().openInventory(internalInventory.get(this.playerClicks.get(event.getPlayer().getUniqueId().toString())));
        }else {
            toggleHarvester(event.getPlayer());
        }
    }

    @Override
    protected Map<String, Object> saveCustomData(Interaction interaction) {
        Map<String, Object> customData = new HashMap<>();
        customData.put("toggled", this.enabledHarvesters.contains(interaction));
        customData.put("inventory-contents", InventoryUtility.convertInventory(this.internalInventory.get(interaction)));
        return customData;
    }

    @Override
    protected void loadCustomData(ConfigurationSection section, Interaction interaction) {
        if(section.getBoolean("toggled"))
            enabledHarvesters.add(interaction);
        String iB64 = section.getString("inventory-contents");
        ItemStack[] contents = InventoryUtility.getSavedInventory(iB64);
        this.internalInventory.get(interaction).setContents(contents);
    }

    private void toggleHarvester(Player player){
        Interaction interaction = this.playerClicks.get(player.getUniqueId().toString());
        if(interaction == null) return;
        if(enabledHarvesters.contains(interaction)){
            enabledHarvesters.remove(interaction);
            ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
            builder.color(Color.BLACK);
            builder.location(interaction.getLocation().add(0,1.5,0));
            builder.offset(0.25,0.25,0.25);
            builder.allPlayers();
            builder.count(25);
            builder.spawn();
            interaction.getWorld().playSound(interaction.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0.5f);
        }else{
            enabledHarvesters.add(interaction);
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

    private void tickHarvester(Interaction interaction){
        Location location = interaction.getLocation();
        Location first = location.clone().add(4, 0, 4);
        Location second = location.clone().subtract(4, 0, 4);

        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.count(10);
        builder.color(Color.YELLOW);
        builder.allPlayers();

        ParticleUtility.squareParticleZone(first, second, builder);

        for(Block crop : ParticleUtility.blocksFromTwoPoints(first, second)){
            if(crop.getBlockData() instanceof Ageable ageable){
                if(ageable.getAge() == 7){
                    Collection<ItemStack> drops = crop.getDrops();
                    ageable.setAge(0);
                    crop.setBlockData(ageable);
                    for(ItemStack item : drops){
                        if(interaction.getLocation().subtract(0 ,1, 0).getBlock().getState() instanceof Chest chest && chest.getBlockInventory().firstEmpty() != -1){
                            chest.getBlockInventory().addItem(item);
                            interaction.getLocation().subtract(0 ,1, 0).getBlock().setBlockData(chest.getBlockData());
                        } else if(interaction.getLocation().subtract(0 ,1, 0).getBlock().getState() instanceof Barrel barrel && barrel.getInventory().firstEmpty() != -1){
                            barrel.getInventory().addItem(item);
                            interaction.getLocation().subtract(0 ,1, 0).getBlock().setBlockData(barrel.getBlockData());
                        }
                        else{
                            this.internalInventory.get(interaction).addItem(item);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void broken(Interaction interaction, ItemDisplay display) {
        if(this.enabledHarvesters.contains(interaction))
            enabledHarvesters.remove(interaction);
        Inventory inventory = internalInventory.remove(interaction);
        ItemStack[] contents = inventory.getContents();
        for(ItemStack item : contents){
            if(item == null) continue;
            interaction.getWorld().dropItem(interaction.getLocation(), item);
        }
    }

    private void tickTimer(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, ()->{
            for(Interaction interaction : enabledHarvesters){
                tickHarvester(interaction);
            }
        }, 2, 2);
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.blockKey), this.blockItem);
        recipe.shape("MHM",
                     "BNB",
                     "MHM");
        recipe.setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        recipe.setIngredient('B', Material.HAY_BLOCK);
        recipe.setIngredient('H', Material.NETHERITE_HOE);
        recipe.setIngredient('N', Material.NETHER_STAR);
        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.blockKey);
    }
}
