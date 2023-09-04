package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class ObjectLockBlock extends CustomBlockModule implements CraftableModule {

    private final Map<Interaction, Location> placedOn;
    private final Map<Interaction, String> ownedBy;

    private final String lockPickKey;
    private final ItemStack lockPick;

    private final BlockFace[] cardinal = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private final Sound lockedSound;
    private final Sound pickingSound;

    public ObjectLockBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("objectLock"));
        this.blockKey = "object-lock";

        placedOn = new HashMap<>();
        ownedBy = new HashMap<>();

        lockedSound = Sound.sound(Key.key("block.chest.locked"), Sound.Source.BLOCK, 1, 1);
        pickingSound = Sound.sound(Key.key("block.chest.locked"), Sound.Source.BLOCK, 1, 2);

        this.lockPickKey = "lock-pick";
        this.lockPick = Hardcore.instance.customItemManager.getCustomItem("lockPick");
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "StandardMagicTools";
    }

    @Override
    protected Map<String, Object> saveCustomData(Interaction interaction) {
        Map<String, Object> customData = new HashMap<>();
        customData.put("placed-on", placedOn.get(interaction));
        customData.put("owned-by", ownedBy.get(interaction));
        return customData;
    }

    @Override
    protected void loadCustomData(ConfigurationSection section, Interaction interaction) {
        Location placedOn = section.getLocation("placed-on");
        String owner = section.getString("owned-by");

        this.ownedBy.put(interaction, owner);
        this.placedOn.put(interaction, placedOn);
    }

    @Override
    protected void onPlace(Interaction interaction, ItemDisplay display) {
        Location location = interaction.getLocation();
        Block locBlock = location.getBlock();

        Vector offset = new Vector(0,0,0);

        Transformation transformation = display.getTransformation();


        Block containerBlock = null;

        for(BlockFace face : cardinal){
            Block block = locBlock.getRelative(face);
            if(block.getState() instanceof BlockInventoryHolder) {
                containerBlock = block;

                switch (face){
                    case NORTH -> {
                        offset = new Vector(0,0,-0.95);
                        transformation = new Transformation(new Vector3f(0.5f),
                                new AxisAngle4f(0f, 0f, 0f, 0f),
                                new Vector3f(1f),
                                new AxisAngle4f(0f, 0f, 0f, 0f));
                    }
                    case EAST -> {
                        offset = new Vector(0.95,0,0);
                        transformation = new Transformation(new Vector3f(0.5f),
                                new AxisAngle4f((float) (-0.5 * Math.PI), 0f, 1, 0f),
                                new Vector3f(1f),
                                new AxisAngle4f(0f, 0f, 0f, 0f));
                    }
                    case WEST -> {
                        offset = new Vector(-0.95,0,0);
                        transformation = new Transformation(new Vector3f(0.5f),
                                new AxisAngle4f((float) (0.5 * Math.PI), 0f, 1, 0f),
                                new Vector3f(1f),
                                new AxisAngle4f(0f, 0f, 0f, 0f));
                    }
                    case SOUTH -> {
                        offset = new Vector(0,0,0.95);
                        transformation = new Transformation(new Vector3f(0.5f),
                                new AxisAngle4f((float)Math.PI, 0f, 1f, 0f),
                                new Vector3f(1f),
                                new AxisAngle4f(0f, 0f, 0f, 0f));
                    }
                }
                break;
            }
        }

        interaction.teleportAsync(interaction.getLocation().add(offset));
        display.setTransformation(transformation);

        if(containerBlock != null){
            Interaction existingLock = getInteractionFromBlock(containerBlock);
            if(existingLock == null && containerBlock.getState() instanceof Chest){
                for(BlockFace face : cardinal){
                    Block adj = containerBlock.getRelative(face);
                    existingLock = getInteractionFromBlock(adj);
                    if(existingLock != null) break;
                }
            }

            if(existingLock == null){
                placedOn.put(interaction, containerBlock.getLocation());
            }

        }

        if(!placedOn.containsKey(interaction)){
            this.breakBlock(null, this.displays.get(interaction), interaction);
        }
    }

    @Override
    protected void placedBy(Interaction interaction, Player whoPlaced) {
        if(whoPlaced == null) return;
        this.ownedBy.put(interaction, whoPlaced.getUniqueId().toString());
    }

    @Override
    protected void breakBlock(Player whoBroke, ItemDisplay display, Entity interaction) {
        Interaction inter = (Interaction) interaction;
        String ownerUUID = this.ownedBy.get(inter);

        if(whoBroke == null){
            if(ownerUUID != null){
                Player player = Bukkit.getPlayer(UUID.fromString(ownerUUID));
                if(player != null){
                    player.sendMessage("&cYour lock has been broken! Was it placed around a container?".convertToComponent());
                }
            }
        }

        if(whoBroke == null || ownerUUID == null || ownerUUID.equals(whoBroke.getUniqueId().toString())){
            super.breakBlock(whoBroke, display, interaction);
            placedOn.remove(inter);
            ownedBy.remove(interaction);
        }else{
            whoBroke.sendMessage("&cYou do not own this Lock!".convertToComponent());
            whoBroke.getWorld().playSound(lockedSound, inter);
        }
    }

    private boolean isLockPick(@Nullable ItemStack itemStack){
        if(itemStack == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(!container.has(this.itemKey)) return false;
        String itemKey = container.get(this.itemKey, PersistentDataType.STRING);
        if(itemKey == null) return false;
        return itemKey.equals(this.lockPickKey);
    }

    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Interaction interaction = this.playerClicks.get(player.getUniqueId().toString());
        Location blockLocation = this.placedOn.get(interaction);
        if(blockLocation == null || blockLocation.getBlock().getType().equals(Material.AIR))
            this.breakBlock(player, this.displays.get(interaction), interaction);
        else {
            Block block = blockLocation.getBlock();
            BlockInventoryHolder blockInventoryHolder = (BlockInventoryHolder)block.getState();
            if(this.ownedBy.get(interaction).equals(player.getUniqueId().toString())){
                player.openInventory(blockInventoryHolder.getInventory());
            }else{
                if(isLockPick(player.getInventory().getItemInMainHand())){
                    Random random = new Random();
                    int chance = random.nextInt(0, 100);
                    if(chance <= 5){
                        player.openInventory(blockInventoryHolder.getInventory());
                        player.sendMessage("&aYou have picked the lock!".convertToComponent());
                    }else{
                        player.damage(4.0f);
                        player.sendMessage("&cYou hurt yourself trying to pick the lock...".convertToComponent());
                    }
                    player.getWorld().playSound(pickingSound, interaction);
                }else{
                    player.sendMessage("&cThis container is locked!".convertToComponent());
                    blockLocation.getWorld().playSound(lockedSound, interaction);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    @Override
    public void onInteract(PlayerInteractEvent event) {
        super.onInteract(event);
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = event.getClickedBlock();
            if(block == null) return;
            Interaction lock = getInteractionFromBlock(block);
            if(lock == null && block.getState() instanceof Chest){
                for(BlockFace face : cardinal){
                    Block adj = block.getRelative(face);
                    lock = getInteractionFromBlock(adj);
                    if(lock != null) break;
                }
            }

            if(lock == null) return;

            String owner = this.ownedBy.get(lock);
            if(owner == null) return;
            if(!owner.equals(event.getPlayer().getUniqueId().toString())){
                event.setCancelled(true);
                event.getPlayer().sendMessage("&cThis container is locked!".convertToComponent());
                event.getPlayer().getWorld().playSound(lockedSound, lock);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block broken = event.getBlock();

        Interaction interaction = getInteractionFromBlock(broken);
        if(interaction == null && broken.getState() instanceof Chest){
            for(BlockFace face : cardinal){
                Block adj = broken.getRelative(face);
                interaction = getInteractionFromBlock(adj);
                if(interaction != null) break;
            }
        }

        if(interaction == null) return;

        Player player = event.getPlayer();
        String ownerUUID = ownedBy.get(interaction);
        if(!player.getUniqueId().toString().equals(ownerUUID)){
            event.setCancelled(true);
            player.sendMessage("&cYou cannot break a locked box!".convertToComponent());
        }
    }

    private Interaction getInteractionFromBlock(Block block){
        for(Interaction interaction : this.placedOn.keySet()){
            if(this.placedOn.get(interaction).equals(block.getLocation())){
                return interaction;
            }
        }
        return null;
    }

    @Override
    public List<Recipe> getMultiRecipe() {
        ShapedRecipe lockRecipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.blockKey), this.blockItem);
        lockRecipe.shape(
                "MIM",
                "IEI",
                "IGI");
        lockRecipe.setIngredient('M', Hardcore.instance.customItemManager.getCustomItem("medCovalDust"));
        lockRecipe.setIngredient('I', Material.IRON_INGOT);
        lockRecipe.setIngredient('E', Material.ENDER_EYE);
        lockRecipe.setIngredient('G', Material.GOLD_INGOT);

        ShapedRecipe pickRecipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + this.lockPickKey), this.lockPick);
        pickRecipe.shape(
                "III",
                "NHH");
        pickRecipe.setIngredient('I', Material.IRON_INGOT);
        pickRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        pickRecipe.setIngredient('H', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));

        return List.of(lockRecipe, pickRecipe);
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + this.blockKey, "crafting-" + this.lockPickKey);
    }

    @Override
    public Recipe getItemRecipe() { return null; }
}
