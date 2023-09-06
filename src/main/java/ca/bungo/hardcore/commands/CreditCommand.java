package ca.bungo.hardcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreditCommand extends Command {

    String credit = "";

    public CreditCommand(@NotNull String name) {
        super(name);
        this.description = "Show credits for the server!";

        buildCredits();
    }

    private void buildCredits(){
        StringBuilder builder = new StringBuilder();
        builder.append("&5Server Credits:\n");

        builder.append("&0---------------------------\n");
        builder.append("&eTraveler Boss Music:&r \n");
        builder.append("\uD83C\uDFB5 Song: 'Dragon Castle' by Makai Symphony\n");
        builder.append("https://www.youtube.com/channel/UC8cn3OdeqYhyhNUyrMxOQKQ\n");

        builder.append("&0---------------------------\n");

        builder.append("&eDeveloped By:&r \n");
        builder.append("-nbarudi \n");
        builder.append("-Bungo \n");

        builder.append("&0---------------------------\n");

        builder.append("&eSpecial Thanks:&r \n");
        builder.append("-sabarudi - Chest Lock Concept\n");
        builder.append("-fearlessmatrix - Traveler Boss Concept\n");


        builder.append("&0---------------------------\n");

        builder.append("&eBeta Testers:&r \n");

        builder.append("&0---------------------------\n");

        credit = builder.toString();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        buildCredits();
        sender.sendMessage(credit.convertToComponent());
        return false;
    }
}
