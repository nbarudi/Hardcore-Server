package ca.bungo.hardcore.modules.types.interfaces;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.entity.Player;

public interface BuyableModule {

    int getCost();
    default String depends(){
        return "BaseModule";
    }

    String getModuleName();

    default boolean canPurchaseModule(Player player){
        HardcorePlayer hardcorePlayer = player.getHardcorePlayer();
        int playerPoints = hardcorePlayer.getPoints();
        return this.getCost() >= playerPoints && hardcorePlayer.hasModule(this.depends());
    }

    default void onPurchase(Player player) {}

    default String friendlyName(){
        return this.getModuleName();
    }

    default String friendlyDescription(){
        return this.friendlyName();
    }

}
