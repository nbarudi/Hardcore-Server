package ca.bungo.hardcore.modules.command;

import ca.bungo.hardcore.modules.types.classes.CommandModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommandModule extends CommandModule implements BuyableModule {

    public FeedCommandModule(@NotNull String name, String commandName) {
        super(name, commandName);
        this.requiresPlayer = true;
        this.hasCooldown = true;
        this.getModuleCommand().setDescription("Fill your hunger!");
    }

    @Override
    public void runCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;

        player.setFoodLevel(20);
        player.setSaturation(20);

        player.getHardcorePlayer().addCooldown(this.getModuleName(), 60, null);
        player.sendMessage(("&eYour hunger has been sated!").convertToComponent());
        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f ,1f);
        ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
        builder.color(Color.fromRGB(64, 39, 11));
        builder.allPlayers();
        builder.count(100);
        builder.offset(1, 2, 1);
        builder.location(player.getLocation());
        builder.spawn();
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public String friendlyName() {
        return "Feed Command";
    }

    @Override
    public String friendlyDescription() {
        return "Gain access to the /feed command!";
    }
}
