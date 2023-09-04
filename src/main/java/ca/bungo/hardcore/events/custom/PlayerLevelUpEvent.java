package ca.bungo.hardcore.events.custom;

import ca.bungo.hardcore.types.HardcorePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancel = false;

    private final HardcorePlayer player;
    private final int lastLevel;
    private final int newLevel;

    public PlayerLevelUpEvent(HardcorePlayer player, int lastLevel, int newLevel){
        this.lastLevel = lastLevel;
        this.newLevel = newLevel;
        this.player = player;
    }

    public HardcorePlayer getPlayer() {
        return player;
    }


    public int getLastLevel() {
        return lastLevel;
    }


    public int getNewLevel() {
        return newLevel;
    }


    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
