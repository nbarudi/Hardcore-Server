package ca.bungo.hardcore.modules.block.custom;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.utility.InventoryUtility;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class FlagClaimBlock extends CustomBlockModule implements CraftableModule {

    private class FlagInventoryHolder implements InventoryHolder {

        private Inventory coreInventory;
        private Inventory colorInventory;
        private Inventory storageInventory;
        private Inventory allyInventory;

        private final ItemStack fillerItem;
        private ItemStack flagColorSection;
        private final ItemStack storageSection;
        private final ItemStack allySection;
        private final String hostFlagUUID;
        private final String worldName;

        public FlagInventoryHolder(String hostFlagUUID, String worldName){
            this.hostFlagUUID = hostFlagUUID;
            this.worldName = worldName;
            fillerItem = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("").build();
            flagColorSection = new ItemStackBuilder(Material.WHITE_WOOL).setName("&fChange Flag Color").build();
            storageSection = new ItemStackBuilder(Material.PAPER).setName("&3Access Claim Storage").build();
            allySection = new ItemStackBuilder(Material.PLAYER_HEAD).setName("&5Manage Your Allies").build();

            buildCoreInventory();
            buildColorInventory();
            buildAllyInventory();
            buildFuelInventory();
        }

        private void updateFlagColor(){
            ItemDisplay display = (ItemDisplay)Bukkit.getWorld(worldName)
                    .getEntity(UUID.fromString(FlagClaimBlock.this.displays.get(hostFlagUUID)));
            ItemStack item = display.getItemStack();
            if(item == null) return;

            ItemMeta meta = item.getItemMeta();
            if(!(meta instanceof LeatherArmorMeta leatherArmorMeta)) return;
            Color color = leatherArmorMeta.getColor();
            DyeColor dyeColor = DyeColor.getByColor(color);
            if(dyeColor == null) return;

            ItemStackBuilder builder = new ItemStackBuilder(Material.AIR);
            builder.from(flagColorSection);
            builder.setType(Material.valueOf(dyeColor.name() + "_WOOL"));
            builder.setName("&#" + Integer.toHexString(color.asRGB()).toUpperCase() + "Change Flag Color");
            flagColorSection = builder.build();
            coreInventory.setItem(1, flagColorSection);
        }

        private void buildCoreInventory(){
            String ownerUUID = FlagClaimBlock.ownedBy.get(hostFlagUUID);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
            coreInventory = Bukkit.createInventory(this, 9, (offlinePlayer.getName() + "'s Claim Manager").convertToComponent());
            for(int i = 0; i < 9; i++)
                coreInventory.setItem(i, fillerItem);
            updateFlagColor();

            coreInventory.setItem(1, flagColorSection);
            coreInventory.setItem(4, storageSection);
            coreInventory.setItem(7, allySection);
        }

        private void buildColorInventory(){
            colorInventory = Bukkit.createInventory(this, 18, "&eChange Your Flag Color".convertToComponent());
            colorInventory.setItem(0, fillerItem);
            colorInventory.setItem(17, fillerItem);
            ItemStackBuilder builder = new ItemStackBuilder(Material.WHITE_WOOL);
            for(DyeColor color : DyeColor.values()){
                builder.setName("&#" + Integer.toHexString(color.getColor().asRGB()).toUpperCase() + "Change Flag Color");
                builder.setType(Material.valueOf(color.name() + "_WOOL"));
                colorInventory.addItem(builder.build());
            }
        }

        private void buildFuelInventory(){
            storageInventory = Bukkit.createInventory(this, 18, "&eClaim Community Storage".convertToComponent());
        }

        private void buildAllyInventory(){
            allyInventory = Bukkit.createInventory(this, 9, "&3Friendly Allies!".convertToComponent());

            for(int i = 0; i < 9; i++)
                allyInventory.setItem(i, fillerItem);

            allyInventory.setItem(1, new ItemStackBuilder(Material.AIR)
                    .from(Hardcore.instance.customItemManager.getCustomItem("biometric"))
                    .setName("&e" + Bukkit.getOfflinePlayer(UUID.fromString(FlagClaimBlock.ownedBy.get(hostFlagUUID))).getName() + "'s Biometric")
                    .addPDC(NamespacedKey.fromString("biometric", Hardcore.instance), FlagClaimBlock.ownedBy.get(hostFlagUUID))
                    .setPlayerHead(FlagClaimBlock.ownedBy.get(hostFlagUUID))
                    .build());
            allyInventory.setItem(4, new ItemStack(Material.AIR));
            allyInventory.setItem(7, new ItemStack(Material.AIR));

            List<String> allies = FlagClaimBlock.allies.get(hostFlagUUID);

            for(String uuid : allies){
                ItemStack biometric = new ItemStackBuilder(Material.AIR)
                        .from(Hardcore.instance.customItemManager.getCustomItem("biometric"))
                        .setName("&e" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + "'s Biometric")
                        .addPDC(NamespacedKey.fromString("biometric", Hardcore.instance), uuid)
                        .setPlayerHead(uuid)
                        .build();
                allyInventory.addItem(biometric);
            }

        }

        @Override
        public @NotNull Inventory getInventory() {
            return coreInventory;
        }

        public Inventory getStorageInventory() { return this.storageInventory; }
        public void setStorageInventory(ItemStack[] contents) { this.storageInventory.setContents(contents); }

        public boolean inventoryInteract(InventoryClickEvent event){
            Player player = (Player) event.getWhoClicked();
            ItemStack stack = event.getCurrentItem();
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if(topInventory.equals(storageInventory)){
                return false;
            }
            if(stack == null) return true;

            if(topInventory.equals(coreInventory)){
                if(stack.equals(flagColorSection)){
                    player.openInventory(colorInventory);
                }
                else if(stack.equals(allySection)){
                    player.openInventory(allyInventory);
                }
                else if(stack.equals(storageSection)){
                    player.openInventory(storageInventory);
                }
            }
            else if(topInventory.equals(colorInventory)){
                String colorName = stack.getType().toString().replace("_WOOL", "");
                DyeColor newColor = DyeColor.valueOf(colorName);
                changeFlagColor(colorName);
                player.closeInventory();
                player.sendMessage(("&eChanged the color to &#" + Integer.toHexString(newColor.getColor().asRGB()) + colorName.toLowerCase() +"&e!").convertToComponent());
                updateFlagColor();
            }
            else if(topInventory.equals(allyInventory)){
                String uuid = getBioUUID(stack);
                if(uuid == null) return true;
                if(uuid.equals(FlagClaimBlock.ownedBy.get(hostFlagUUID))) return true;
            }
            return true;
        }

        public void inventoryClose(InventoryCloseEvent event){
            Inventory closedInventory = event.getInventory();

            if(closedInventory.equals(allyInventory)){

                ItemStack ally2 = allyInventory.getItem(4);
                ItemStack ally3 = allyInventory.getItem(7);
                FlagClaimBlock.allies.get(hostFlagUUID).clear();
                if(ally2 != null){
                    String allyUUID = getBioUUID(ally2);
                    if(allyUUID == null) return;
                    FlagClaimBlock.allies.get(hostFlagUUID).add(allyUUID);
                }
                if(ally3 != null){
                    String allyUUID = getBioUUID(ally3);
                    if(allyUUID == null) return;
                    FlagClaimBlock.allies.get(hostFlagUUID).add(allyUUID);
                }

            }
        }

        public void changeFlagColor(String colorName){
            if(colorName == null)
                colorName = "BLACK";
            DyeColor newColor = DyeColor.valueOf(colorName);

            ItemDisplay display = (ItemDisplay)Bukkit.getWorld(worldName)
                    .getEntity(UUID.fromString(FlagClaimBlock.this.displays.get(hostFlagUUID)));
            if(display.getItemStack() == null) return;
            ItemStackBuilder builder = new ItemStackBuilder(Material.AIR);
            builder.from(display.getItemStack());
            builder.setColor(newColor.getColor());
            display.setItemStack(builder.build());
            updateFlagColor();

            FlagClaimBlock.this.flagColor.put(hostFlagUUID, colorName);
        }
    }

    private static final Map<String, String> ownedBy = new HashMap<>();
    private static final Map<String, List<String>> allies = new HashMap<>();
    private final Map<String, BlockFace> facing;
    private static final Map<String, Long> claimedChunks = new HashMap<>();

    private final Map<String, FlagInventoryHolder> interactionInventory;
    private final Map<String, String> flagColor;

    public FlagClaimBlock(String moduleName) {
        super(moduleName, Hardcore.instance.customItemManager.getCustomItem("claimFlag"));
        this.blockKey = "claim-flag";

        this.facing = new HashMap<>();
        this.interactionInventory = new HashMap<>();
        this.flagColor = new HashMap<>();
    }

    @Override
    protected Map<String, Object> saveCustomData(Interaction interaction) {
        Map<String, Object> customData = new HashMap<>();
        customData.put("facing", facing.get(interaction.getUniqueId().toString()).name());
        customData.put("owned-by", ownedBy.get(interaction.getUniqueId().toString()));
        customData.put("chunk", claimedChunks.get(interaction.getUniqueId().toString()));
        customData.put("color", flagColor.get(interaction.getUniqueId().toString()));
        customData.put("allies", allies.get(interaction.getUniqueId().toString()));
        customData.put("storage", InventoryUtility.convertInventory(interactionInventory.get(interaction.getUniqueId().toString()).getStorageInventory()));
        return customData;
    }

    @Override
    protected void loadCustomData(ConfigurationSection section, Interaction interaction) {
        String owner = section.getString("owned-by");
        BlockFace face = BlockFace.valueOf(section.getString("facing"));
        long chunk = section.getLong("chunk");
        String color = section.getString("color");
        List<String> allies = section.getStringList("allies");
        String iB64 = section.getString("storage");

        ownedBy.put(interaction.getUniqueId().toString(), owner);
        this.facing.put(interaction.getUniqueId().toString(), face);
        claimedChunks.put(interaction.getUniqueId().toString(), chunk);
        this.flagColor.put(interaction.getUniqueId().toString(), color);
        FlagClaimBlock.allies.put(interaction.getUniqueId().toString(), allies);

        updateFlag(interaction, iB64);
    }

    private void updateFlag(Interaction interaction, @Nullable String savedStorage){
        ItemDisplay display = (ItemDisplay)interaction.getWorld()
                .getEntity(UUID.fromString(FlagClaimBlock.this.displays.get(interaction.getUniqueId().toString())));
        Transformation transformation = display.getTransformation();
        BlockFace face = facing.get(interaction.getUniqueId().toString());
        if(face == null) return;
        switch (face){
            case NORTH -> transformation = new Transformation(new Vector3f(0.5f),
                    new AxisAngle4f(0f, 0f, 0f, 0f),
                    new Vector3f(1f),
                    new AxisAngle4f(0f, 0f, 0f, 0f));
            case EAST -> transformation = new Transformation(new Vector3f(0.5f),
                    new AxisAngle4f((float) (-0.5 * Math.PI), 0f, 1, 0f),
                    new Vector3f(1f),
                    new AxisAngle4f(0f, 0f, 0f, 0f));
            case WEST -> transformation = new Transformation(new Vector3f(0.5f),
                    new AxisAngle4f((float) (0.5 * Math.PI), 0f, 1, 0f),
                    new Vector3f(1f),
                    new AxisAngle4f(0f, 0f, 0f, 0f));
            case SOUTH -> transformation = new Transformation(new Vector3f(0.5f),
                    new AxisAngle4f((float)Math.PI, 0f, 1f, 0f),
                    new Vector3f(1f),
                    new AxisAngle4f(0f, 0f, 0f, 0f));
        }
        display.setTransformation(transformation);
        interactionInventory.put(interaction.getUniqueId().toString(), new FlagInventoryHolder(interaction.getUniqueId().toString(),
                interaction.getWorld().getName()));
        interactionInventory.get(interaction.getUniqueId().toString()).changeFlagColor(flagColor.get(interaction.getUniqueId().toString()));

        if(savedStorage != null){
            interactionInventory.get(interaction.getUniqueId().toString()).setStorageInventory(InventoryUtility.getSavedInventory(savedStorage));
        }
    }

    @Override
    protected void onPlace(Interaction interaction, ItemDisplay display) {
        allies.put(interaction.getUniqueId().toString(), new ArrayList<>());
        updateFlag(interaction, null);

        Chunk chunk = interaction.getChunk();
        if(isChunkClaimed(chunk)){
            breakBlock(null, display, interaction);
        }else{
            flagColor.put(interaction.getUniqueId().toString(), "BLACK");
            claimedChunks.put(interaction.getUniqueId().toString(), chunk.getChunkKey());
            String owner = ownedBy.get(interaction.getUniqueId().toString());
            if(owner != null){
                Player player = Bukkit.getPlayer(UUID.fromString(owner));
                if(player != null)
                    player.sendMessage("&aYou have claimed this chunk!".convertToComponent());
            }
        }
    }

    @Override
    protected void breakBlock(Player whoBroke, ItemDisplay display, Entity interaction) {
        Interaction inter = (Interaction) interaction;
        String ownerUUID = ownedBy.get(inter.getUniqueId().toString());

        if(whoBroke == null){
            if(ownerUUID != null){
                Player player = Bukkit.getPlayer(UUID.fromString(ownerUUID));
                if(player != null){
                    player.sendMessage("&cYou have failed to claim this Chunk! &eIs it already claimed?".convertToComponent());
                }
            }
        }

        if(whoBroke == null || ownerUUID == null || ownerUUID.equals(whoBroke.getUniqueId().toString())){
            super.breakBlock(whoBroke, display, interaction);
            ownedBy.remove(interaction.getUniqueId().toString());
            facing.remove(interaction.getUniqueId().toString());
            claimedChunks.remove(interaction.getUniqueId().toString());
            flagColor.remove(interaction.getUniqueId().toString());
            interactionInventory.remove(interaction.getUniqueId().toString());
            allies.remove(interaction.getUniqueId().toString());
            if(whoBroke != null)
                whoBroke.sendMessage("&cYou have unclaimed this chunk!".convertToComponent());
        }else{
            whoBroke.sendMessage("&cYou do not own this claim!".convertToComponent());
        }
    }

    @Override
    public Recipe getItemRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Hardcore.instance, "crafting-" + blockKey), blockItem);
        recipe.shape(
                "HLW",
                "HLW",
                "HLO");
        recipe.setIngredient('H', Hardcore.instance.customItemManager.getCustomItem("highCovalDust"));
        recipe.setIngredient('L', new RecipeChoice.MaterialChoice(Tag.LOGS));
        recipe.setIngredient('W', new RecipeChoice.MaterialChoice(Tag.WOOL));
        recipe.setIngredient('O', Hardcore.instance.customItemManager.getCustomItem("objectLock"));
        return recipe;
    }

    @Override
    public boolean requiresModuleToCreate() {
        return true;
    }

    @Override
    public String overrideModuleName() {
        return "AdvancedMagicTools";
    }

    @Override
    public List<String> getCraftingKeys() {
        return List.of("crafting-" + blockKey);
    }

    public static boolean isChunkClaimed(Chunk chunk){
        for(Map.Entry<String, Long> claimed : claimedChunks.entrySet()){
            Entity entity = chunk.getWorld().getEntity(UUID.fromString(claimed.getKey()));
            if(entity == null) continue;
            Chunk claimedChunk = entity.getChunk();
            if(chunk.getChunkKey() == claimedChunk.getChunkKey())
                return true;
        }
        return false;
    }

    public static List<String> getChunkOwner(Chunk chunk){
        List<String> owners = new ArrayList<>();
        if(!isChunkClaimed(chunk)) return null;
        for(Map.Entry<String, Long> claimed : claimedChunks.entrySet()) {
            Entity entity = chunk.getWorld().getEntity(UUID.fromString(claimed.getKey()));
            if(entity == null) continue;
            Chunk claimedChunk = entity.getChunk();
            if (chunk.getChunkKey() == claimedChunk.getChunkKey()) {
                owners.add(ownedBy.get(claimed.getKey()));
                owners.addAll(allies.get(claimed.getKey()));
                break;
            }
        }
        return owners;
    }

    private String getBioUUID(ItemStack itemStack){
        if(!itemStack.getType().equals(Material.PLAYER_HEAD)) return null;
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString("biometric", Hardcore.instance);
        if(key == null) return null;
        return container.get(key, PersistentDataType.STRING);
    }

    @Override
    protected void placedBy(Interaction interaction, Player whoPlaced) {
        if(whoPlaced == null) return;
        ownedBy.put(interaction.getUniqueId().toString(), whoPlaced.getUniqueId().toString());
        facing.put(interaction.getUniqueId().toString(), whoPlaced.getFacing());
    }
    @Override
    protected void interactWithBlock(PlayerInteractEntityEvent event) {
        Interaction interaction = (Interaction) event.getRightClicked();
        String ownerUUID = ownedBy.get(interaction.getUniqueId().toString());

        if(!event.getPlayer().isSneaking()){
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
            event.getPlayer().sendMessage(("&eThis Chunk is owned by: &b" + player.getName()).convertToComponent());
        }else{
            List<String> owners = getChunkOwner(interaction.getChunk());
            if(owners != null && owners.contains(event.getPlayer().getUniqueId().toString())){
                event.getPlayer().openInventory(interactionInventory.get(interaction.getUniqueId().toString()).getInventory());
            }else{
                event.getPlayer().sendMessage("&4You do not own this claim!".convertToComponent());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;

        if(inventory.getHolder() instanceof FlagInventoryHolder flagInventoryHolder){
            event.setCancelled(flagInventoryHolder.inventoryInteract(event));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getHolder() instanceof FlagInventoryHolder flagInventoryHolder){
            flagInventoryHolder.inventoryClose(event);
        }
    }


    //Stop the flag from attaching to a horse.
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Horse){
            if(this.verifyItem(event.getPlayer().getInventory().getItemInMainHand())
                    || this.verifyItem(event.getPlayer().getInventory().getItemInOffHand())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        if(isChunkClaimed(chunk)){
            List<String> owners = getChunkOwner(chunk);
            if(owners == null || owners.isEmpty()) return;
            if(!owners.contains(player.getUniqueId().toString())){
                event.setCancelled(true);
                player.sendMessage(("&4This chunk has been claimed by " +
                        Bukkit.getOfflinePlayer(UUID.fromString(owners.get(0))).getName()).convertToComponent());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        if(isChunkClaimed(chunk)){
            List<String> owners = getChunkOwner(chunk);
            if(owners == null || owners.isEmpty()) return;
            if(!owners.contains(player.getUniqueId().toString())){
                event.setCancelled(true);
                player.sendMessage(("&4This chunk has been claimed by " +
                        Bukkit.getOfflinePlayer(UUID.fromString(owners.get(0))).getName()).convertToComponent());
            }
        }
    }
}
