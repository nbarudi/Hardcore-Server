package ca.bungo.hardcore.modules.types.classes;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.block.custom.FlagClaimBlock;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public abstract class CustomBlockModule extends Module implements Listener {

    protected final Map<Interaction, ItemDisplay> displays;
    protected final Map<String, Interaction> playerClicks;
    protected final ItemStack blockItem;
    protected String blockKey;

    public CustomBlockModule(String moduleName, ItemStack blockItem) {
        super(moduleName);
        this.blockItem = blockItem;
        this.cost = blockItem;
        this.costAmount = 1;
        this.displays = new HashMap<>();
        this.playerClicks = new HashMap<>();

        Bukkit.getScheduler().runTaskLater(Hardcore.instance, this::loadCustomBlocks, 5);
    }

    protected void onPlace(Interaction interaction, ItemDisplay display) {}
    protected void placedBy(Interaction interaction, Player whoPlaced) {}

    protected Interaction placeBlock(Location location, Player placedBy){
        ItemDisplay _display = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        _display.setItemStack(this.blockItem);
        _display.setDisplayHeight(1);
        _display.setDisplayWidth(1);
        _display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
        _display.setTransformation(new Transformation(new Vector3f(0.5f),
                new AxisAngle4f(0f, 0f, 0f, 0f),
                new Vector3f(1f),
                new AxisAngle4f(0f, 0f, 0f, 0f)));
        Vector3f vec = _display.getTransformation().getTranslation();
        Interaction interaction = (Interaction) location.getWorld().spawnEntity(location, EntityType.INTERACTION);
        interaction.teleport(location.add(vec.x, 0, vec.z));
        displays.put(interaction, _display);
        placedBy(interaction, placedBy);
        onPlace(interaction, _display);
        return interaction;
    }

    protected abstract void interactWithBlock(PlayerInteractEntityEvent event);


    protected boolean verifyItem(@NotNull ItemStack toCompare) {
        this.verifyPersist();
        if(toCompare.getItemMeta() == null) return false;
        PersistentDataContainer container = toCompare.getItemMeta().getPersistentDataContainer();
        if(!container.has(this.itemKey)) return false;
        String containerString = container.get(this.itemKey, PersistentDataType.STRING);
        if(containerString == null) return false;
        return containerString.equals(blockKey);
    }

    private void verifyPersist(){
        ItemMeta meta = blockItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(!container.has(this.itemKey, PersistentDataType.STRING)){
            container.set(this.itemKey, PersistentDataType.STRING, "castingItem");
            blockItem.setItemMeta(meta);
        }
    }

    protected void broken(Interaction interaction, ItemDisplay display) {}

    @Override
    protected boolean passesCost(Player player){
        if(!this.hasCost || this.cost == null) return true;
        PlayerInventory inventory = player.getInventory();
        if(inventory.containsAtLeast(cost, costAmount)){
            return true;
        }
        if(this.costMessage != null)
            player.sendMessage(this.costMessage);
        return false;
    }

    protected void removeItem(Player player){
        PlayerInventory inventory = player.getInventory();
        ItemStack costItem = cost.clone();
        costItem.setAmount(this.costAmount);
        inventory.removeItemAnySlot(costItem);
    }

    protected Map<String, Object> saveCustomData(Interaction interaction) {return new HashMap<>();}

    public ItemStack getBlockItem() {
        return this.blockItem;
    }

    public void saveCustomBlocks(){
        for(ItemDisplay display : this.displays.values()){
            FileConfiguration config = Hardcore.instance.getConfig();
            config.set("custom-blocks." + display.getEntityId() + ".type", this.getModuleName());
            config.set("custom-blocks." + display.getEntityId() + ".location", display.getLocation());
        }
        for(Interaction interaction : this.displays.keySet()){
            ItemDisplay display = this.displays.get(interaction);
            FileConfiguration config = Hardcore.instance.getConfig();
            Map<String, Object> customData = saveCustomData(interaction);
            for(String key : customData.keySet()){
                config.set("custom-blocks." + display.getEntityId() + "." + key, customData.get(key));
            }
        }
        this.displays.forEach((i, d) ->{
            i.remove();
            d.remove();
        });

        this.displays.clear();
        Hardcore.instance.saveConfig();
    }

    protected void loadCustomData(ConfigurationSection section, Interaction interaction) {}
    public void loadCustomBlocks() {
        ConfigurationSection section = Hardcore.instance.getConfig().getConfigurationSection("custom-blocks");
        if(section == null) section = Hardcore.instance.getConfig().createSection("custom-blocks");
        for(String key : section.getKeys(false)){
            String type = section.getString(key + ".type");
            Location location = section.getLocation(key + ".location");
            if(location == null) continue;
            if(this.getModuleName().equals(type)){
                loadCustomData(section.getConfigurationSection(key), this.placeBlock(location, null));
                section.set(key, null);
            }
        }
        Hardcore.instance.saveConfig();
    }

    private boolean debounce = false;
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(this.blockItem == null) return;
        if(debounce) return;
        if(this.verifyItem(player.getInventory().getItemInMainHand()) || this.verifyItem(player.getInventory().getItemInOffHand()))
            if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(this.passesCost(player)){
                    if(event.getClickedBlock() != null &&
                            FlagClaimBlock.isChunkClaimed(event.getClickedBlock().getChunk())){
                        List<String> owners = FlagClaimBlock.getChunkOwner(event.getClickedBlock().getChunk());
                        if(owners != null && !owners.contains(event.getPlayer().getUniqueId().toString())){
                            event.getPlayer().sendMessage("&4You do not own this claim!".convertToComponent());
                            return;
                        }
                    }
                    Block block = event.getClickedBlock();
                    BlockFace face = event.getBlockFace();
                    assert block != null;
                    block = block.getRelative(face);
                    if(!block.getLocation().getNearbyEntities(0, 0, 0).isEmpty()) return;
                    placeBlock(block.getLocation(), player);
                    removeItem(player);
                }
            }
        debounce = true;
        Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> debounce = false, 5);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event){
        if(event.getRightClicked().getType().equals(EntityType.INTERACTION)){
            Interaction interaction = (Interaction) event.getRightClicked();
            ItemDisplay display = displays.get(interaction);
            if(display != null){
                this.playerClicks.put(event.getPlayer().getUniqueId().toString(), interaction);
                interactWithBlock(event);
            }
            debounce = true;
            Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> debounce = false, 5);
        }
    }

    protected void breakBlock(Player whoBroke, ItemDisplay display, Entity interaction){
        interaction.remove();
        display.remove();
        this.displays.remove((Interaction) interaction);
        display.getLocation().getWorld().dropItem(display.getLocation(), this.blockItem);
        this.broken((Interaction) interaction, display);
    }

    @EventHandler
    public void onAttemptedBreak(PrePlayerAttackEntityEvent event){
        Player player = event.getPlayer();
        if(event.getAttacked().getType().equals(EntityType.INTERACTION)){
            ItemDisplay display = this.displays.get((Interaction) event.getAttacked());
            if(display != null){
                if(FlagClaimBlock.isChunkClaimed(display.getChunk())){
                    List<String> owners = FlagClaimBlock.getChunkOwner(display.getChunk());
                    if(owners != null && !owners.contains(event.getPlayer().getUniqueId().toString())){
                        event.getPlayer().sendMessage("&4You do not own this claim!".convertToComponent());
                        return;
                    }
                }
                breakBlock(player, display, event.getAttacked());
            }
        }
    }

}
