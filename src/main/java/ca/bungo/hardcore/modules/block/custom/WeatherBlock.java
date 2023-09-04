package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class WeatherBlock extends CustomBlockModule implements CraftableModule {

    private Inventory customInventory;


    private ItemStack sun;

    private ItemStack rain;

    private ItemStack thunder;

    private final ItemStack fuelItem;

    public WeatherBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("weatherBlock"));
        this.blockKey = "weather-block";

        this.loadInventory();
        this.fuelItem = Hardcore.instance.customItemManager.getCustomItem("fuelItem");
    }

    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        player.openInventory(customInventory);
    }


    private void loadInventory(){
        this.customInventory = Bukkit.createInventory(null, 27, ("&eWeather Controller").convertToComponent());

        ItemStack filler = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName("")
                .build();

        thunder = new ItemStackBuilder(Material.PAPER)
                .addPDC(this.itemKey, "weather-thunder")
                .setName("&eThunder")
                .setCustomModelData(1)
                .addLore("&6Set the Weather to Thunder!")
                .addLore("&eCost: 64 Mystical Fuel")
                .build();

        rain = new ItemStackBuilder(Material.PAPER)
                .addPDC(this.itemKey, "weather-rain")
                .setName("&3Rainy")
                .setCustomModelData(1)
                .addLore("&6Set the Weather to Rain!")
                .addLore("&eCost: 64 Mystical Fuel")
                .build();

        sun = new ItemStackBuilder(Material.PAPER)
                .addPDC(this.itemKey, "weather-sun")
                .setName("&6Sunny")
                .setCustomModelData(1)
                .addLore("&6Set the Weather to Sun!")
                .addLore("&eCost: 64 Mystical Fuel")
                .build();

        customInventory.setItem(10, sun);
        customInventory.setItem(13, rain);
        customInventory.setItem(16, thunder);
        while(customInventory.firstEmpty() != -1){
            customInventory.setItem(customInventory.firstEmpty(), filler);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() == null || !event.getClickedInventory().equals(this.customInventory)) return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        ItemStack stack = event.getCurrentItem();
        if(stack == null) return;

        if(!player.getInventory().containsAtLeast(fuelItem, 64)) {
            player.sendMessage(("&cYou do not have enough fuel to control the weather!").convertToComponent());
            player.closeInventory();
            return;
        }

        ItemStack cost = fuelItem.clone();
        cost.setAmount(64);

        if(stack.equals(sun)){
            Bukkit.getLogger().info("Making the Sun!");
            changeWeather("sun", player);
            player.getInventory().removeItemAnySlot(cost);
        }
        else if(stack.equals(rain)){
            Bukkit.getLogger().info("Making the Rain!");
            changeWeather("rain", player);
            player.getInventory().removeItemAnySlot(cost);
        }
        else if(stack.equals(thunder)){
            Bukkit.getLogger().info("Making the Thunder!");
            changeWeather("thunder", player);
            player.getInventory().removeItemAnySlot(cost);
        }

        player.closeInventory();
    }

    private void changeWeather(String type, Player player){
        Interaction interaction = this.playerClicks.get(player.getUniqueId().toString());
        if(interaction == null) return;

        Location start = interaction.getLocation();

        final double[] yPos = {0};

        ParticleBuilder extra = new ParticleBuilder(Particle.REDSTONE);
        ParticleBuilder base = new ParticleBuilder(Particle.REDSTONE);
        base.color(Color.WHITE);
        switch (type.toLowerCase()){
            case "sun":
                extra.color(Color.YELLOW);
                break;
            case "rain":
                extra.color(Color.BLUE);
                break;
            case "thunder":
                extra.color(Color.BLACK);
                break;
        }

        final int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () ->{
            while(yPos[0] < 300){
                Location _center = start.clone();
                yPos[0] += 0.25;

                _center.add(0, yPos[0], 0);

                extra.location(_center);
                extra.spawn();

                base.location(_center);
                base.spawn();
            }
            yPos[0] = 0;
        }, 10, 10);

        Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> {
            switch (type.toLowerCase()){
                case "sun":
                    player.getWorld().setStorm(false);
                    player.getWorld().setThundering(false);
                    break;
                case "rain":
                    player.getWorld().setStorm(true);
                    player.getWorld().setThundering(false);
                    break;
                case "thunder":
                    player.getWorld().setStorm(true);
                    player.getWorld().setThundering(true);
                    break;
            }
            Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> Bukkit.getScheduler().cancelTask(task), 30);
        }, 60);

    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.blockKey), this.blockItem);
        recipe.shape(
                "XYX",
                "YRY",
                "XYX");

        recipe.setIngredient('X', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('Y', Material.DEEPSLATE_BRICKS);
        recipe.setIngredient('R', Hardcore.instance.customItemManager.getCustomItem("lightningRod"));
        return recipe;
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.blockKey);
    }
}
