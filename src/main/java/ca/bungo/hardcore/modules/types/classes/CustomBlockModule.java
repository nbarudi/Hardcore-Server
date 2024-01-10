package ca.bungo.hardcore.modules.types.classes;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.block.custom.FlagClaimBlock;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.*;
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

    public final Map<String, String> displays;
    protected final Map<String, String> playerClicks;
    protected final ItemStack blockItem;
    protected String blockKey;

    public CustomBlockModule(String moduleName, ItemStack blockItem) {
        super(moduleName);
        this.blockItem = blockItem;
        this.cost = blockItem;
        this.costAmount = 1;
        this.displays = new HashMap<>();
        this.playerClicks = new HashMap<>();

        //Bukkit.getScheduler().runTaskLater(Hardcore.instance, this::loadCustomBlocks, 5);
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

        String uuid = UUID.randomUUID().toString();
        interaction.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("custom-block-uuid", Hardcore.instance)),
                PersistentDataType.STRING, uuid);
        interaction.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("custom-block-type", Hardcore.instance)),
                PersistentDataType.STRING, this.getModuleName());

        displays.put(interaction.getUniqueId().toString(), _display.getUniqueId().toString());
        placedBy(interaction, placedBy);
        onPlace(interaction, _display);
        return interaction;
    }

    /*
    public void fixBlockData(String uuid, Interaction interaction){
        Interaction toFix = null;
        ItemDisplay active = null;

        for(Map.Entry<Interaction, ItemDisplay> displays : displays.entrySet()){

            String existingUUID = displays.getKey().getPersistentDataContainer()
                    .get(Objects.requireNonNull(NamespacedKey.fromString("custom-block-uuid", Hardcore.instance)), PersistentDataType.STRING);
            if(existingUUID == null) continue;
            if(existingUUID.equals(uuid)){
                toFix = displays.getKey();;
                active = displays.getValue();
                break;
            }
        }

        if(toFix != null){
            displays.remove(toFix);
            displays.put(interaction, active);
        }
    }*/

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

    public void unloadInChunk(Chunk chunk){

        boolean isChunkLoaded = false;

        for(Player player : Bukkit.getOnlinePlayers()){
            int playerChunkX = player.getLocation().getChunk().getX();
            int playerChunkZ = player.getLocation().getChunk().getZ();

            int targetChunkX = chunk.getX();
            int targetChunkZ = chunk.getZ();

            int distanceX = Math.abs(playerChunkX - targetChunkX);
            int distanceZ = Math.abs(playerChunkZ - targetChunkZ);

            int viewDistance = Bukkit.getViewDistance();

            if (distanceX <= viewDistance && distanceZ <= viewDistance) {
                isChunkLoaded = true;
                break;
            }
        }

        if(isChunkLoaded) return;

        List<Interaction> toUnload = new ArrayList<>();
        for(String displayuuid : this.displays.values()){
            if(chunk.getWorld().getEntity(UUID.fromString(displayuuid)) == null) continue;
            ItemDisplay display = (ItemDisplay) chunk.getWorld().getEntity(UUID.fromString(displayuuid));
            if(display.getChunk().getChunkKey() != chunk.getChunkKey()) continue;

            FileConfiguration config = Hardcore.instance.getConfig();
            config.set("custom-blocks." + display.getEntityId() + ".type", this.getModuleName());
            config.set("custom-blocks." + display.getEntityId() + ".location", display.getLocation());
        }
        for(String interactionUUID : this.displays.keySet()){
            if(chunk.getWorld().getEntity(UUID.fromString(interactionUUID)) == null) continue;
            Interaction interaction = (Interaction) chunk.getWorld().getEntity(UUID.fromString(interactionUUID));
            String displayUUID = this.displays.get(interactionUUID);
            ItemDisplay display = (ItemDisplay) chunk.getWorld().getEntity(UUID.fromString(displayUUID));
            if(display.getChunk().getChunkKey() != chunk.getChunkKey()) continue;

            toUnload.add(interaction);
            FileConfiguration config = Hardcore.instance.getConfig();
            Map<String, Object> customData = saveCustomData(interaction);
            for(String key : customData.keySet()){
                config.set("custom-blocks." + display.getEntityId() + "." + key, customData.get(key));
            }
        }

        for(Interaction interaction : toUnload){
            interaction.remove();
            ItemDisplay display = (ItemDisplay) interaction.getWorld()
                    .getEntity(UUID.fromString(this.displays.get(interaction.getUniqueId().toString())));
            display.remove();
        }
        Hardcore.instance.saveConfig();

    }

    public void loadInChunk(Chunk chunk){

        ConfigurationSection section = Hardcore.instance.getConfig().getConfigurationSection("custom-blocks");
        if(section == null) section = Hardcore.instance.getConfig().createSection("custom-blocks");
        for(String key : section.getKeys(false)){
            String type = section.getString(key + ".type");
            Location location = section.getLocation(key + ".location");
            if(location == null) continue;

            if(location.getChunk().getChunkKey() != chunk.getChunkKey()) continue;

            if(this.getModuleName().equals(type)){
                loadCustomData(section.getConfigurationSection(key), this.placeBlock(location, null));
                section.set(key, null);
            }
        }
        Hardcore.instance.saveConfig();

    }

    public void saveCustomBlocks(){
        for(String displayuuid : this.displays.values()){
            for(World world : Bukkit.getWorlds()){
                if(world.getEntity(UUID.fromString(displayuuid)) == null) continue;
                ItemDisplay display = (ItemDisplay) world.getEntity(UUID.fromString(displayuuid));
                FileConfiguration config = Hardcore.instance.getConfig();
                config.set("custom-blocks." + display.getEntityId() + ".type", this.getModuleName());
                config.set("custom-blocks." + display.getEntityId() + ".location", display.getLocation());
                break;
            }
        }
        for(String interactionUUID : this.displays.keySet()){
            for(World world : Bukkit.getWorlds()){
                if(world.getEntity(UUID.fromString(interactionUUID)) == null) continue;
                Interaction interaction = (Interaction) world.getEntity(UUID.fromString(interactionUUID));
                String displayUUID = this.displays.get(interactionUUID);
                ItemDisplay display = (ItemDisplay) world.getEntity(UUID.fromString(displayUUID));
                FileConfiguration config = Hardcore.instance.getConfig();
                Map<String, Object> customData = saveCustomData(interaction);
                for(String key : customData.keySet()){
                    config.set("custom-blocks." + display.getEntityId() + "." + key, customData.get(key));
                }
                break;
            }

        }
        this.displays.forEach((i, d) ->{
            for(World world : Bukkit.getWorlds()){
                if(world.getEntity(UUID.fromString(i)) == null) continue;
                world.getEntity(UUID.fromString(i)).remove();
                world.getEntity(UUID.fromString(d)).remove();
                break;
            }
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
            String displayuuid = displays.get(interaction.getUniqueId().toString());
            if(displayuuid == null) return;
            ItemDisplay display = (ItemDisplay) interaction.getWorld().getEntity(UUID.fromString(displayuuid));
            if(display != null){
                this.playerClicks.put(event.getPlayer().getUniqueId().toString(), interaction.getUniqueId().toString());
                interactWithBlock(event);
            }
            debounce = true;
            Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> debounce = false, 5);
        }
    }

    protected void breakBlock(Player whoBroke, ItemDisplay display, Entity interaction){
        interaction.remove();
        display.remove();
        this.displays.remove(interaction.getUniqueId().toString());
        display.getLocation().getWorld().dropItem(display.getLocation(), this.blockItem);
        this.broken((Interaction) interaction, display);
    }

    @EventHandler
    public void onAttemptedBreak(PrePlayerAttackEntityEvent event){
        Player player = event.getPlayer();
        if(event.getAttacked().getType().equals(EntityType.INTERACTION)){
            String displayuuid = this.displays.get(event.getAttacked().getUniqueId().toString());
            if(displayuuid == null) return;
            ItemDisplay display = (ItemDisplay)event.getAttacked().getWorld().getEntity(UUID.fromString(displayuuid));
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
