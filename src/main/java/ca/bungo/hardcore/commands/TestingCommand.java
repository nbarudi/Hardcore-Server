package ca.bungo.hardcore.commands;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestingCommand extends Command {

    public TestingCommand(@NotNull String name) {
        super(name);
        this.setPermission("hardcore.admin");
        this.description = "Testing Command";
    }

    int _temp = 0;

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {

        if(!(commandSender instanceof Player player)) return false;

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("give")){
                if(args.length > 1){
                    String itemName = args[1];
                    if(!Hardcore.instance.customItemManager.getItemNames().contains(itemName))
                        player.sendMessage("&cInvalid item name!".convertToComponent());
                    else
                        player.getInventory().addItem(Hardcore.instance.customItemManager.getCustomItem(itemName));
                }else{
                    player.sendMessage("&bListing Items:".convertToComponent());
                    for(String name : Hardcore.instance.customItemManager.getItemNames()){
                        player.sendMessage(("&e" + name).convertToComponent());
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("module")){
                if(args.length > 1){
                    String moduleName = args[1];
                    Module module = Hardcore.instance.moduleManager.getModuleByName(moduleName);
                    if(module != null){
                        player.getHardcorePlayer().addModule(module);
                        player.sendMessage(("&eAdded Module &b" + module.getModuleName() + " &eto self!").convertToComponent());
                    }else{
                        player.sendMessage("&4Error: Invalid Module Name!".convertToComponent());
                    }
                }else {
                    player.sendMessage("&bListing Modules:".convertToComponent());
                    for(String name : Hardcore.instance.moduleManager.getModuleNames()){
                        player.sendMessage(("<hover:show_text:'&4Click to add module " + name + "'><click:run_command:'/test module " + name + "'>&e" + name + "</click>").convertToComponent());
                    }
                }
            }

            else if(args[0].equalsIgnoreCase("level")){
                HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
                if(args.length == 2){
                    switch (args[1].toLowerCase()){
                        case "up":
                            hardcorePlayer.setExperience((hardcorePlayer.getLevel()*500 + 1000));
                            player.sendMessage("&eLeveled you up!".convertToComponent());
                            break;
                        case "down":
                            hardcorePlayer.setExperience(-1);
                            player.sendMessage("&eReduced your level!".convertToComponent());
                            break;
                    }
                }
                else if(args.length == 3){
                    if(!args[1].equalsIgnoreCase("set")){
                        player.sendMessage("&cInvalid Usage!".convertToComponent());
                    }else{
                        try{
                            int newLevel = Integer.parseInt(args[2]);
                            hardcorePlayer.setExperience(0);
                            hardcorePlayer.setLevel(newLevel);
                        } catch(NumberFormatException e){
                            player.sendMessage("&cInvalid Number!".convertToComponent());
                        }
                    }
                }
            }

            else if(args[0].equalsIgnoreCase("boss")){
                if(args.length != 2){
                    player.sendMessage("&cInvalid Usage!".convertToComponent());
                    return false;
                }

                String bossName = args[1];

                if(!Hardcore.instance.bossManager.spawnBoss(bossName, player.getLocation())){
                    player.sendMessage("&4Failed to spawn the boss.. Does it already exist?".convertToComponent());
                }else{
                    player.sendMessage("&aSpawned custom boss!".convertToComponent());
                }

            }
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        if(args.length == 1)
            return List.of("give", "module", "level", "boss");
        else if(args.length == 2 && args[0].equalsIgnoreCase("give")){
            return Hardcore.instance.customItemManager.getItemNames().stream().toList();
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("module")){
            return Hardcore.instance.moduleManager.getModuleNames().stream().toList();
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("level")){
            return List.of("up", "down", "set");
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("boss")){
            return Hardcore.instance.bossManager.getBossMap().keySet().stream().toList();
        }

        return super.tabComplete(sender, alias, args);
    }
}
