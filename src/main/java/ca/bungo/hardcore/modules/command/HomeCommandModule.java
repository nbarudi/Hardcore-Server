package ca.bungo.hardcore.modules.command;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.managers.FileManager;
import ca.bungo.hardcore.modules.types.classes.CommandModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class HomeCommandModule extends CommandModule implements BuyableModule {

    private final YamlConfiguration cfg;
    private final FileManager.Config config;

    public HomeCommandModule(@NotNull String name) {
        super(name, "home", List.of("sethome", "delhome"));
        this.hasCooldown = true;
        this.requiresPlayer = true;

        this.getModuleCommand().setDescription("Teleport Back To Your Home!");

        this.config = Hardcore.instance.fileManager.getConfig("homes.yml");
        this.cfg = config.get();
    }

    @Override
    public void runCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();

        if(label.equalsIgnoreCase("sethome")){
            String homeName = "home";

            ConfigurationSection section = cfg.getConfigurationSection("players." + player.getUniqueId());
            if(section == null)
                section = cfg.createSection("players." + player.getUniqueId());
            Set<String> homes = section.getKeys(false);
            int maxHomes = 1;
            if(hardcorePlayer.hasModule("ExtraHomesOneModule"))
                maxHomes++;
            if(hardcorePlayer.hasModule("ExtraHomesTwoModule"))
                maxHomes++;

            if(homes.size() >= maxHomes){
                player.sendMessage(("&cError! You have already reached the maximum number of homes!").convertToComponent());
                return;
            }

            if(args.length > 0)
                homeName = args[0];

            section.set(homeName, player.getLocation());
            player.sendMessage(("&aSet home &e" + homeName + " &ato your location!").convertToComponent());
        }
        else if(label.equalsIgnoreCase("delhome")){
            ConfigurationSection section = cfg.getConfigurationSection("players." + player.getUniqueId());
            if(section == null){
                player.sendMessage(("&cError! You do not have any homes set!").convertToComponent());
                return;
            }

            if(args.length == 0){
                player.sendMessage(("&cInvalid Usage /delhome <Home Name>").convertToComponent());
                return;
            }

            section.set(args[0], null);
            player.sendMessage(("&aAttempted to remove home &e" + args[0]).convertToComponent());
        } else{

            ConfigurationSection section = cfg.getConfigurationSection("players." + player.getUniqueId());
            if(section == null){
                player.sendMessage(("&cError! You do not have any homes set!").convertToComponent());
                return;
            }

            if(args.length == 0){
                player.sendMessage(("&eHomes:").convertToComponent());
                for(String key : section.getKeys(false))
                    player.sendMessage(("&b- " + key).convertToComponent());

                return;
            }

            Location location = section.getLocation(args[0]);
            if(location == null){
                player.sendMessage(("&cError! The Home &e" + args[0] + " &cdoes not exist!").convertToComponent());
                return;
            }
            player.sendMessage(("&aStarting teleport to home &e" + args[0]).convertToComponent());

            Hardcore.instance.teleportUtility.teleportWithDelaySound(player, location);

            if(hardcorePlayer.hasModule("LowerHomesCooldownTwoModule")){
                player.getHardcorePlayer().addCooldown(this.getModuleName(), 10, null);
            }
            else if(hardcorePlayer.hasModule("LowerHomesCooldownOneModule")){
                player.getHardcorePlayer().addCooldown(this.getModuleName(), 15, null);
            }
            else {
                player.getHardcorePlayer().addCooldown(this.getModuleName(), 20, null);
            }
        }

        config.save();
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String friendlyName() {
        return "Home Command";
    }

    @Override
    public String friendlyDescription() {
        return "Gain the ability to teleport back to your set location!";
    }
}
