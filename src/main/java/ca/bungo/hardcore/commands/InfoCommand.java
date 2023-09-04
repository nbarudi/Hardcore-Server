package ca.bungo.hardcore.commands;

import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoCommand extends Command {

    public InfoCommand(String name){
        super(name);
        this.description = "Get information about your character!";
        this.usageMessage = "/" + this.getName();
        this.setAliases(List.of("stats"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;

        player.sendMessage("&aObtaining Player Info...".convertToComponent());

        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        player.sendMessage(hardcorePlayer.toString().convertToComponent());

        return false;
    }
}
