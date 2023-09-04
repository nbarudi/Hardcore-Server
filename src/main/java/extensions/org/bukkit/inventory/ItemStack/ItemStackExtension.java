package extensions.org.bukkit.inventory.ItemStack;

import ca.bungo.hardcore.utility.ItemStackBuilder;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.bukkit.inventory.ItemStack;

@Extension
public class ItemStackExtension {
    public static ItemStackBuilder getItemStackBuilder(@This ItemStack internal) {
        ItemStackBuilder builder = new ItemStackBuilder(internal.getType());
        builder.from(internal);
        return builder;
    }
}
