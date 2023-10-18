package ca.bungo.hardcore.utility;

import ca.bungo.hardcore.Hardcore;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder {

    ItemStack item;
    ItemMeta meta;

    public ItemStackBuilder(@NotNull Material itemMaterial){
        item = new ItemStack(itemMaterial);
        meta = item.getItemMeta();
    }

    public ItemStackBuilder setType(@NotNull Material type){
        item.setType(type);
        return this;
    }

    public ItemStackBuilder setAmount(int amount){
        item.setAmount(amount);
        return this;
    }

    public ItemStackBuilder setName(@NotNull String name){
        meta.displayName(name.convertToComponent());
        return this;
    }

    public ItemStackBuilder addLore(@NotNull String loreToAdd){
        List<Component> currentLore = meta.lore();
        if(currentLore == null)
            currentLore = new ArrayList<>();
        currentLore.add(loreToAdd.convertToComponent());
        meta.lore(currentLore);
        return this;
    }

    public ItemStackBuilder clearLore(){
        meta.lore(new ArrayList<>());
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level){
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemStackBuilder removeEnchantment(Enchantment enchantment){
        meta.removeEnchant(enchantment);
        return this;
    }

    public ItemStackBuilder addFlag(ItemFlag flag){
        meta.addItemFlags(flag);
        return this;
    }

    public ItemStackBuilder removeFlag(ItemFlag flag){
        meta.removeItemFlags(flag);
        return this;
    }

    public ItemStackBuilder addPDC(NamespacedKey namespace, String value){
        meta.getPersistentDataContainer().set(namespace, PersistentDataType.STRING, value);
        return this;
    }

    public ItemStackBuilder addCustomPDC(NamespacedKey namespace, PersistentDataType type, Object value){
        meta.getPersistentDataContainer().set(namespace, type, value);
        return this;
    }

    public ItemStackBuilder setCustomModelData(int data){
        meta.setCustomModelData(data);
        return this;
    }

    public ItemStackBuilder from(ItemStack item){
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
        return this;
    }

    public ItemStackBuilder setAttribute(Attribute attribute, AttributeModifier modifier){
        Bukkit.getLogger().info("Modifier: " + this.meta.getAttributeModifiers(Attribute.GENERIC_ARMOR));
        this.meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemStackBuilder setColor(Color color){
        if(this.item.getType().toString().toLowerCase().contains("potion")){
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(color);
            this.meta = potionMeta;
        }
        else if(this.item.getType().toString().toLowerCase().contains("leather") && this.item.getType().toString().toLowerCase().contains("armor")){
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
            leatherArmorMeta.setColor(color);
            this.meta = leatherArmorMeta;
        }
        else{
            Hardcore.instance.getLogger().severe("Attempting to Color Non-Colorable Item!");
        }
        return this;
    }

    public ItemStackBuilder setPlayerHead(String uuid){
        if(meta instanceof SkullMeta skullMeta){
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
            meta = skullMeta;
        }
        return this;
    }

    public ItemStackBuilder setUnbreakable(boolean unbreakable){
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build(){
        item.setItemMeta(meta);
        return item;
    }


    public static Object getCustomPDC(NamespacedKey key, ItemStack stack, PersistentDataType type){
        ItemMeta _meta = stack.getItemMeta();
        PersistentDataContainer container = _meta.getPersistentDataContainer();
        return container.get(key, type);
    }

}
