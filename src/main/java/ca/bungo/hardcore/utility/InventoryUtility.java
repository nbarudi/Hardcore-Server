package ca.bungo.hardcore.utility;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InventoryUtility {

    public static String convertInventory(Inventory inventory) {

        try {
            //Save this string to file.
            return inventoryToBase64(inventory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack[] getSavedInventory(String data) {

        try {
            return inventoryFromBase64(data).getContents();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ItemStack[0];
    }

    public static String inventoryToBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(inventory.getSize());

            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

            //Converts the inventory and its contents to base64, This also saves item meta-data and inventory type
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert inventory to base64.", e);
        }
    }

    public static Inventory inventoryFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException("Could not decode inventory.", e);
        }
    }

}
