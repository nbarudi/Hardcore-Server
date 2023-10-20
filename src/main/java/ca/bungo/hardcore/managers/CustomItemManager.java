package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.utility.ItemStackBuilder;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {

    private final Map<String, ItemStack> customItems;

    private final NamespacedKey itemKey;

    public CustomItemManager(){
        this.customItems = new HashMap<>();
        this.itemKey = new NamespacedKey(Hardcore.instance, "hardcore-items");

        registerItems();
    }

    public NamespacedKey getItemKey(){
        return this.itemKey;
    }

    private void registerItems(){
        customItems.put("testItem", new ItemStackBuilder(Material.PAPER)
                .setName("&eCustom Item")
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addPDC(itemKey, "custom-test-item")
                .build()
        );

        customItems.put("lowCovalDust", new ItemStackBuilder(Material.SUGAR)
                .setName("&#296e16Low Covalence Dust")
                .addLore("&eA slight magical energy is radiating from this material")
                .setCustomModelData(1)
                .addPDC(itemKey, "low-coval-dust")
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("medCovalDust", new ItemStackBuilder(Material.SUGAR)
                .setName("&#23917bMedium Covalence Dust")
                .addLore("&eA noticable magical energy is radiating from this material")
                .setCustomModelData(2)
                .addPDC(itemKey, "med-coval-dust")
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("highCovalDust", new ItemStackBuilder(Material.SUGAR)
                .setName("&#152a8aHigh Covalence Dust")
                .addLore("&eA strong magical energy is radiating from this material")
                .setCustomModelData(3)
                .addPDC(itemKey, "high-coval-dust")
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("fuelItem", new ItemStackBuilder(Material.GLOWSTONE_DUST)
                        .setName("&#FFFD42Mystical Fuel")
                        .addLore("&eA potent fuel for magical items")
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addPDC(itemKey, "magic-fuel")
                        .setCustomModelData(1)
                        .build());

        customItems.put("oreLocator1", new ItemStackBuilder(Material.COMPASS)
                        .setName("&#296e16Simple Ore Locator")
                        .addPDC(this.itemKey, "ore-locate-1")
                        .addLore("&eRight-Click this item to locate near-by ores!")
                        .setCustomModelData(1)
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .build());

        customItems.put("oreLocator2", new ItemStackBuilder(Material.COMPASS)
                .setName("&#23917bOre Locator")
                .addPDC(this.itemKey, "ore-locate-2")
                .addLore("&eRight-Click this item to locate near-by ores!")
                .setCustomModelData(2)
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("oreLocator3", new ItemStackBuilder(Material.COMPASS)
                .setName("&#152a8aStrong Ore Locator")
                .addPDC(this.itemKey, "ore-locate-3")
                .addLore("&eRight-Click this item to locate near-by ores!")
                .setCustomModelData(3)
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("lightningRod", new ItemStackBuilder(Material.STICK)
                        .setName("&eFlash Rod")
                        .addPDC(this.itemKey, "lightning-rod")
                        .addLore("&cSmite down your enemies!")
                        .addLore("&4&oHopefully my luck is not that bad...")
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .setCustomModelData(1)
                        .build());

        customItems.put("lightningInABottle", new ItemStackBuilder(Material.GLASS_BOTTLE)
                        .setName("&r&eLightning in a Bottle")
                        .addLore("&cSome how, you have captured the storm itself")
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                        .setCustomModelData(1)
                .build());

        customItems.put("weatherBlock", new ItemStackBuilder(Material.PAPER)
                        .setCustomModelData(1)
                        .setName("&eWeather Controller")
                        .addLore("&cSeems you can control the weather itself with this!")
                        .addPDC(this.itemKey, "weather-block")
                .build());

        customItems.put("travelersBook", new ItemStackBuilder(Material.WRITTEN_BOOK)
                        .setName("<!i>&eTravelers Book")
                        .addLore("&cAn Ancient book lost in the caves...")
                        .addLore("&6I wonder what information you might be able to find...")
                .build());

        customItems.put("chargedHeart", new ItemStackBuilder(Material.APPLE)
                        .setName("&eCharged &4Heart")
                        .addLore("&6'I do hope.. that you will use my power well...'")
                        .addLore("&e - &eT&ch&ae &k&b0&50&70&b0&10&20&30&40")
                        .addPDC(this.itemKey, "charged-heart")
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                        .setCustomModelData(1)
                .build());

        customItems.put("handCannon", new ItemStackBuilder(Material.PAPER)
                        .setName("<!i>&eHand Cannon")
                        .addLore("&cA Very Heavy weapon used to launch explosive bullets")
                        .setCustomModelData(2)
                        .addPDC(this.itemKey, "hand-cannon")
                .build());

        customItems.put("superWatch", new ItemStackBuilder(Material.FEATHER)
                .setCustomModelData(1)
                .setName("&l&aSuper Clock")
                .addLore("&cSeems you can control the Time using this clock!")
                .addPDC(this.itemKey, "superclock-block")
                .build());

        customItems.put("harvester", new ItemStackBuilder(Material.PAPER)
                .setCustomModelData(3)
                .setName("<!i>&eHarvester")
                .addLore("&aAutomatically harvest crops within a 4x4 radius of the harvester!")
                .addLore("&eCrouch and Right-Click to open internal inventory!")
                .addPDC(this.itemKey, "harvester-block")
                .build());

        customItems.put("playerModTable", new ItemStackBuilder(Material.PAPER)
                .setCustomModelData(4)
                .setName("<!i>&cPlayer Modification Table")
                .addLore("&dGain skills to achieve great things!")
                .addPDC(this.itemKey, "playermodtable-block")
                .build());

        customItems.put("armorSpeedMod", new ItemStackBuilder(Material.FEATHER)
                .setCustomModelData(2)
                .setName("<!i>&bArmor Speed Modifier")
                .addLore("Use this in a smithing table to lower the weight of your armor!")
                .addLore("<!i>&4NOTE: This does &lNOT<!b> stack on items! It will Overwrite previous values!")
                .addLore("<!i>&eOnly Usable on Boots!")
                .addPDC(this.itemKey, "armor-speed-mod")
                .build());

        customItems.put("armorKnockbackMod", new ItemStackBuilder(Material.FEATHER)
                .setCustomModelData(3)
                .setName("<!i>&bArmor Knockback Resistance Modifier")
                .addLore("Use this in a smithing table to anchor you to the ground!")
                .addLore("<!i>&4NOTE: This does &lNOT<!b> stack on items! It will Overwrite previous values!")
                .addLore("<!i>&eOnly Usable on Leggings!")
                .addPDC(this.itemKey, "armor-knockback-mod")
                .build());

        customItems.put("objectLock", new ItemStackBuilder(Material.PAPER)
                .setCustomModelData(5)
                .setName("<!i>&eContainer Lock")
                .addLore("&dLock your Chest, Furnace, or Other Storage Objects to only yourself!")
                .addPDC(this.itemKey, "object-lock")
                .build());

        customItems.put("lockPick", new ItemStackBuilder(Material.PAPER)
                .setCustomModelData(6)
                .setName("<!i>&5Lock Pick")
                .addLore("&dDeal with those pesky locks the better way!")
                .addLore("&eNote: You must click on the lock to pick!")
                .addLore("&c5% Chance of successful unlock!")
                .addLore("&bFailing a pick might hurt...")
                .addPDC(this.itemKey, "lock-pick")
                .build());

        customItems.put("claimFlag", new ItemStackBuilder(Material.LEATHER_HORSE_ARMOR)
                        .setName("<!i>&aLand Claim")
                        .setColor(DyeColor.BLACK.getColor())
                        .addLore("<!i>&eClaim the Chunk for Yourself!")
                        .addPDC(this.itemKey, "claim-flag")
                        .addFlag(ItemFlag.HIDE_DYE)
                        .setCustomModelData(1)
                .build());

        customItems.put("biometric", new ItemStackBuilder(Material.PLAYER_HEAD)
                .setName("<!i>&eNAME'S Biometric")
                .addLore("<!i>&3A Biometric Record")
                .addPDC(NamespacedKey.fromString("biometric", Hardcore.instance), "none")
                .build());

        customItems.put("biometricReader", new ItemStackBuilder(Material.PAPER)
                .setName("<!i>&4Biometric Reader")
                .setCustomModelData(7)
                .addPDC(this.itemKey, "biometric-reader")
                .addLore("&eRight-Click to take a Reading of Your Self!")
                .build());

        customItems.put("golemsThoughts", new ItemStackBuilder(Material.POPPY)
                .setName("<!i>&dA Golems Thoughts")
                .addLore("<!i>&eYou're getting closer to &k&b000000000000")
                .addPDC(this.itemKey, "golems-thoughts")
                .setCustomModelData(1)
                .addEnchantment(Enchantment.ARROW_INFINITE, -1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build());

        customItems.put("travelersWishes", new ItemStackBuilder(Material.NETHER_STAR)
                .setName("<!i>&6The Travelers Wishes")
                .addLore("<!i>&4Forgive me for what I have done...")
                .addLore("<!i>&9For at some point along the way...")
                .addLore("<!i>&6I abandoned my oath of knowledge...")
                .addLore("<!i>&5and began searching for power...")
                .addPDC(this.itemKey, "travelers-wishes")
                .build());

        customItems.put("nyanGun", new ItemStackBuilder(Material.DIAMOND_SWORD)
                .setName("<!i><rainbow>Nyan Pew")
                .addLore("<!i><rainbow>Nya Nya Nya Nya Nya")
                .addPDC(this.itemKey, "nyan-gun")
                .setCustomModelData(1)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .setUnbreakable(true)
                .build());

        customItems.put("grappleHook", new ItemStackBuilder(Material.FISHING_ROD)
                .setName("<!i>&8Grapple Hook")
                .addLore("<!i>It's a bird! It's a plane! It's You!")
                .addPDC(this.itemKey, "grapple-hook")
                .setUnbreakable(true)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .build());

        customItems.put("bagOfHolding", new ItemStackBuilder(Material.STICK)
                .setName("<!i>&eBag of Holding")
                .addLore("<!i>&aStore your items in here!")
                .addLore("&4Items stored in this NBT!")
                .addLore("&4If you lose this you lose your items!")
                .addPDC(this.itemKey, "bag-of-holding")
                .setCustomModelData(2)
                .build());

        customItems.put("spectralShears", new ItemStackBuilder(Material.SHEARS)
                .setName("<!i>&bSpectral Shears")
                .addLore("<!i>&eSnip a soul from a mob!")
                .addLore("<!i>&cI'm sure they wont mind... Right?")
                .addPDC(this.itemKey, "spectral-shears")
                .setCustomModelData(1)
                .build());

        customItems.put("soulShard", new ItemStackBuilder(Material.GHAST_TEAR)
                .setName("<!i>&bSoul Shard")
                .setCustomModelData(1)
                .build());
    }

    public Collection<String> getItemNames() {
        return customItems.keySet();
    }


    public ItemStack getCustomItem(String name){
        return customItems.get(name);
    }


}
