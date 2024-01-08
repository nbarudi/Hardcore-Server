package ca.bungo.hardcore.features.bosses;


import ca.bungo.hardcore.Hardcore;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This supports TimerMethod types. What this means is you can name a function like the following:
 * timer30S() -> and this will trigger every 30 seconds.
 * timer1M() -> once every minute
 * timer5T() -> once every 5 ticks
 * */
public abstract class Boss implements Listener {

    private final String name;
    protected BossBar bossBar;
    protected String entityName = "";
    protected Random random = new Random();

    protected final Map<String, Double> damageCount;

    private final List<TimerMethod> timerMethods;

    private static class TimerMethod {
        private final Method timerMethod;
        private final Object classInstance;
        private int internalTicks;

        private final int ticksToCall;

        public TimerMethod(Method timerMethod, Object classInstance, int ticksToCall){
            this.timerMethod = timerMethod;
            this.ticksToCall = ticksToCall;
            this.classInstance = classInstance;
        }

        public void increaseTicks() throws InvocationTargetException, IllegalAccessException {
            this.internalTicks++;

            if(this.internalTicks >= ticksToCall){
                timerMethod.setAccessible(true);
                this.timerMethod.invoke(classInstance);
                timerMethod.setAccessible(false);
                internalTicks = 0;
            }
        }

    }

    protected LivingEntity self;

    protected ModeledEntity modeledEntity = null;
    protected ActiveModel activeModel = null;

    public Boss(String name) {
        this.name = name;

        this.timerMethods = new ArrayList<>();
        damageCount = new HashMap<>();

        initTimerFunctions();
        Bukkit.registerTickTimer(this);
    }

    boolean debounce = false;
    protected boolean canSpawn(){
        return self == null && !debounce;
    }

    protected void applyModel(String model){
        if(self == null) return;
        ModeledEntity _modeled = ModelEngineAPI.createModeledEntity(self);
        ActiveModel _active = ModelEngineAPI.createActiveModel(model);
        if(_active == null) return;

        _modeled.addModel(_active, true);
        modeledEntity = _modeled;
        modeledEntity.setBaseEntityVisible(false);
        activeModel = _active;
    }

    protected void playAnimation(String animName){
        AnimationHandler animationHandler = activeModel.getAnimationHandler();
        animationHandler.playAnimation(animName, 0.3, 0.3, 1, true);
    }

    public boolean spawnSelf(EntityType type, Location location){
        if(!this.canSpawn()) return false;
        self = (LivingEntity) location.getWorld().spawnEntity(location, type);

        if(!this.entityName.isEmpty()){
            self.setCustomNameVisible(true);
            self.customName(this.entityName.convertToComponent());
        }

        bossBar = BossBar.bossBar(this.entityName.convertToComponent(), 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        bossBar.addFlag(BossBar.Flag.PLAY_BOSS_MUSIC);
        bossBar.addFlag(BossBar.Flag.DARKEN_SCREEN);

        return true;
    }

    private void updateBossBar(){
        AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(maxHealth == null) {
            bossBar.progress(0);
            return;
        }
        bossBar.progress((float)(self.getHealth() / maxHealth.getValue()));
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getLocation().distance(self.getLocation()) > 30) player.hideBossBar(bossBar);
            else player.showBossBar(bossBar);
        }
    }

    public abstract void onDeath(EntityDeathEvent event);


    public String getName() { return this.name; }
    public LivingEntity getSelf() { return this.self; }

    @EventHandler
    public void onServerTick(ServerTickEndEvent event){
        if(self == null) {
            if(bossBar != null){
                bossBar.progress(0);
                for(Player player : Bukkit.getOnlinePlayers()){
                    BossBar localBar = bossBar;
                    Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> {
                        player.hideBossBar(localBar);
                    }, 40);
                }
                bossBar = null;
            }
            return;
        }
        if(self.isDead()){
            self = null;
            return;
        }
        updateBossBar();
        for(TimerMethod timerMethod : timerMethods){
            try {
                timerMethod.increaseTicks();
            } catch(InvocationTargetException | IllegalAccessException e) {
                Bukkit.getLogger().severe("Failed to call method: " + timerMethod.timerMethod.getName());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity().equals(self)){
            debounce = true;
            self = null;
            Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> { debounce = false; }, 10);
            onDeath(event);

            Bukkit.broadcast(("&eThe " + this.entityName + " &ehas been slain!").convertToComponent());
            Bukkit.broadcast(("&eHere are the damage results: ").convertToComponent());
            for(String uuid : damageCount.keySet()){
                Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                if(player == null) continue;
                Bukkit.broadcast(("&b - " + player.getName() + " &4" + Math.floor(damageCount.get(uuid)) + " &bDamage!").convertToComponent());
            }

            damageCount.clear();

        }
    }

    @EventHandler
    public void onAttacked(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity attacked = event.getEntity();
        if(!attacked.equals(self)) return;

        if(attacker instanceof Player player){
            if(damageCount.containsKey(player.getUniqueId().toString()))
                damageCount.put(player.getUniqueId().toString(), damageCount.get(player.getUniqueId().toString()) + event.getDamage());
            else
                damageCount.put(attacker.getUniqueId().toString(), event.getDamage());
        }
        else if(attacker instanceof Projectile projectile){
            ProjectileSource shooter = projectile.getShooter();
            if(shooter instanceof Player player){
                if(damageCount.containsKey(player.getUniqueId().toString()))
                    damageCount.put(player.getUniqueId().toString(), damageCount.get(player.getUniqueId().toString()) + event.getDamage());
                else
                    damageCount.put(attacker.getUniqueId().toString(), event.getDamage());
            }
        }
    }

    protected String getMostDamage(){
        String uuid = "";
        double damage = 0;
        for(String _uuid : damageCount.keySet()){
            Player player = Bukkit.getPlayer(UUID.fromString(_uuid));
            if(uuid.isEmpty()){
                uuid = _uuid;
                damage = damageCount.get(_uuid);
            }else if(damage < damageCount.get(_uuid)){
                uuid = _uuid;
                damage = damageCount.get(_uuid);
            }
        }
        return uuid;
    }

    private void initTimerFunctions(){
        Method[] internalMethods = this.getClass().getDeclaredMethods();
        for(Method method : internalMethods){
            if(method.getName().startsWith("timer")){
                //This is a Timer Function
                String timer = method.getName().replace("timer", "");
                int ticks = convertTime(timer);
                TimerMethod timerMethod = new TimerMethod(method, this, ticks);
                this.timerMethods.add(timerMethod);
            }
        }
    }

    private int convertTime(String time){
        String type = time.split("")[time.length()-1];
        String amount = time.replace(type, "");
        int ticks = 0;
        try{
            ticks = Integer.parseInt(amount);
        } catch(NumberFormatException e){
            Bukkit.getLogger().severe("Invalid Timer Method In: " + this.getClass().getName());
            return -1;
        }

        if(type.equalsIgnoreCase("M")){
            ticks = 20*60*ticks;
        }
        else if(type.equalsIgnoreCase("S")){
            ticks = 20*ticks;
        }

        return ticks;
    }

    protected void messageAllInRange(String message, int radius){
        if(self == null) return;
        for(Entity entity : self.getNearbyEntities(radius, radius, radius)){
            if(entity instanceof Player player){
                player.sendMessage(("&7[" + entityName + "&7]: &r" + message).convertToComponent());
            }
        }
    }

    protected void cancelTask(int task){
        Bukkit.getScheduler().cancelTask(task);
    }

}
