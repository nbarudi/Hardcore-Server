package ca.bungo.hardcore.utility.pdc;

import ca.bungo.hardcore.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class InventoryPDC implements PersistentDataType<String, Inventory> {


    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Inventory> getComplexType() {
        return Inventory.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Inventory complex, @NotNull PersistentDataAdapterContext context) {
        String converted = InventoryUtility.convertInventory(complex);
        if(converted == null)
            return "";
        return converted;
    }

    @Override
    public @NotNull Inventory fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        try{
            return InventoryUtility.inventoryFromBase64(primitive);
        } catch(IOException exception){
            exception.printStackTrace();
        }
        return Bukkit.createInventory(null, 9);
    }
}
