package ca.bungo.hardcore.modules.types.classes;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandModule extends Module {

    public class ModuleCommandExecutor extends Command {
        public ModuleCommandExecutor(@NotNull String name) {
            super(name);
        }

        public ModuleCommandExecutor(@NotNull String name, List<String> alias) {
            super(name);
            this.setAliases(alias);
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            if(CommandModule.this.requiresPlayer){
                if(!(sender instanceof Player player)) return false;
                HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
                if(hardcorePlayer.hasModule(CommandModule.this.getModuleName())){
                    if(CommandModule.this.passesCooldown(player) && CommandModule.this.passesCost(player))
                        runCommand(sender, commandLabel, args);
                }else{
                    player.sendMessage(("&cYou do not have this module unlocked yet!").convertToComponent());
                }
            }else{
                runCommand(sender, commandLabel, args);
            }
            return false;
        }
    }

    protected boolean requiresPlayer;
    protected String commandName;
    protected List<String> alias;

    public CommandModule(@NotNull String name, String commandName) {
        super(name);
        this.commandName = commandName;
    }

    public CommandModule(@NotNull String name, String commandName, List<String> alias) {
        super(name);
        this.commandName = commandName;
        this.alias = alias;
    }

    public Command getModuleCommand(){
        if(alias == null)
            return new ModuleCommandExecutor(this.commandName);
        else
            return new ModuleCommandExecutor(this.commandName, this.alias);
    }

    public abstract void runCommand(CommandSender sender, String label, String[] args);



}
