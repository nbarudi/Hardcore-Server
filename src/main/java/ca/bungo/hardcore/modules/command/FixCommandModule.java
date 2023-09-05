package ca.bungo.hardcore.modules.command;

import ca.bungo.hardcore.modules.types.classes.CommandModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class FixCommandModule extends CommandModule implements BuyableModule {

    public FixCommandModule(@NotNull String name, String commandName) {
        super(name, commandName);
        this.requiresPlayer = true;
        this.hasCooldown = true;
        this.getModuleCommand().setDescription("Fix all your gear!");
    }

    @Override
    public void runCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        player.getHardcorePlayer().addCooldown(this.getModuleName(), 120, null);

        for(ItemStack itemStack : player.getInventory()){
            if(itemStack == null) continue;

            if(itemStack.getItemMeta() instanceof Damageable damageable && damageable.hasDamage()){
                damageable.setDamage(0);
                itemStack.setItemMeta(damageable);
                player.sendMessage("&eYour ".convertToComponent()
                        .append(itemStack.displayName().hoverEvent(itemStack.asHoverEvent()))
                        .append(" &eHas been repaired!".convertToComponent()));
            }
        }
        player.sendMessage("&aRepaired all of your items!".convertToComponent());
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String friendlyName() {
        return "Fix Command";
    }

    @Override
    public String friendlyDescription() {
        return "Gain access to the /fix command!";
    }
}
