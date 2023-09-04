package ca.bungo.hardcore.types;

import ca.bungo.hardcore.Hardcore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Cooldown {

    private boolean completed;
    private int ticks;
    private int startingTicks;
    private int task;
    private ItemStack assocItem = null;

    public Cooldown(int ticks){
        this.ticks = ticks;
        this.startingTicks = ticks;
        this.startCooldown();
    }

    public Cooldown(int ticks, ItemStack assocItem){
        this.ticks = ticks;
        this.startCooldown();
        this.assocItem = assocItem;
    }

    public void setAssocItem(ItemStack stack){
        this.assocItem = stack;
    }

    public boolean isCompleted() { return this.completed; }
    private void complete(){
        this.completed = true;
        Bukkit.getScheduler().cancelTask(task);
    }

    private void startCooldown(){
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, ()->{
            if(this.ticks <= 0){
                this.complete();
                return;
            }
            this.ticks--;
        }, 1, 1);
    }

    public int getTicks() { return this.ticks; }
    public int getStartingTicks() { return this.startingTicks; }

    public ItemStack getAssocItem(){
        return this.assocItem;
    }

    public Component getCooldownMessage(){
        double phase = 1 - ((double)this.getTicks()/this.getStartingTicks());
        return ("<transition:red:dark_green:" + phase + ">On Cooldown | Time Remaining: &e" + formatTime()).convertToComponent();
    }

    private String formatTime(){
        String message = "";
        int days = 0;
        int hours = 0;
        int minutes = 0;
        double seconds = this.getTicks()/20.0;
        if(seconds > 60){
            minutes = (int) (seconds / 60);
            seconds = seconds%60;
        }
        if(minutes > 60){
            hours = minutes/60;
            minutes = minutes%60;
        }
        if(hours > 24){
            days = hours/24;
            hours = hours%24;
        }
        if(days > 0)
            message += days + (days == 1 ? " day " : " days ");
        if(hours > 0)
            message += hours + (hours == 1 ? " hour " : " hours ");
        if(minutes > 0)
            message += minutes + (minutes == 1 ? " minute " : " minutes ");
        message += String.format("%.2f", seconds) + " seconds";
        return message;
    }


}
