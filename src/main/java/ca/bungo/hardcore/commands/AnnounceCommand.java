package ca.bungo.hardcore.commands;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import net.minecraft.sounds.SoundSource;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnounceCommand extends Command {

    private enum MessageType {
        TOAST,TITLE,CHAT,UNKNOWN;
    }

    public AnnounceCommand(@NotNull String name) {
        super(name);
        this.description = "Send an announcement to all players";
        this.usageMessage = "/" + this.getName() + " <Toast/Title/Chat> <Message>";
        this.setPermission("hardcore.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if(args.length < 2){
            sender.sendMessage(("&4Invalid Usage: " + this.usageMessage).convertToComponent());
            return false;
        }

        MessageType type = MessageType.UNKNOWN;
        try {
            type = MessageType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        if(type.equals(MessageType.UNKNOWN)){
            sender.sendMessage(("&4Invalid Usage: " + this.usageMessage).convertToComponent());
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!b>&f");
        for(int i = 1; i < args.length; i++){
            builder.append(args[i]).append(" ");
        }
        String message = builder.substring(0, builder.length()-1);
        switch(type){
            case CHAT -> {
                Bukkit.broadcast("&4&lAnnouncement".convertToComponent());
                Bukkit.broadcast(message.convertToComponent());
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1, 0.5f));
                }
            }
            case TITLE -> {
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.showTitle(Title.title("&4&lAnnouncement".convertToComponent(), message.convertToComponent()));
                    player.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1, 0.5f));
                }
            }
            case TOAST -> {
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendFlare("&4&lAnnouncement".convertToComponent(), message.convertToComponent());
                }
            }
        }
        //ToDo: Maybe also send announcement to a discord server if I ever make one
        sender.sendMessage("&eSent announcement!".convertToComponent());
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1){
            return List.of("toast", "title", "chat");
        }
        return super.tabComplete(sender, alias, args);
    }
}
