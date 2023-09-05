package ca.bungo.hardcore.features.bosses.types;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.types.timings.TickTimer;
import ca.bungo.hardcore.utility.MathUtility;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class SuperCreeperBoss extends Boss {

    private NamespacedKey localKey;
    private double projectileDamage = 0;
    private boolean allowProjectileDamage = true;

    /**
     * This supports TimerMethod types. What this means is you can name a function like the following:
     * timer30S() -> and this will trigger every 30 seconds.
     * timer1M() -> once every minute
     * timer5T() -> once every 5 ticks
     *
     * @param name
     */
    public SuperCreeperBoss(String name) {
        super(name);
        this.localKey = new NamespacedKey(Hardcore.instance, "creeper-boss");
    }

    private boolean finishedSpawning = false;
    private int spawnTask = 0;
    private void timer10T(){
        if(finishedSpawning) {
            Bukkit.getScheduler().cancelTask(spawnTask);
            self.setInvulnerable(false);
            self.setAI(true);
            self.setHealth(self.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            bossBar.color(BossBar.Color.YELLOW);
            self.setFireTicks(0);
            finishedSpawning = false;
        }
        if(((Creeper) self).getFuseTicks() > 50){
            self.getWorld().createExplosion(self.getLocation(), 5f, false, false);
            ((Creeper) self).setFuseTicks(0);
        }
    }

    @Override
    public boolean spawnSelf(EntityType type, Location location) {
        this.entityName = "&4Thundering &2Creeper";
        if (!super.spawnSelf(EntityType.CREEPER, location)) return false;
        finishedSpawning = false;
        Creeper selfCreeper = (Creeper) self;
        selfCreeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);

        selfCreeper.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));

        selfCreeper.setInvulnerable(true);
        selfCreeper.setAI(false);
        selfCreeper.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "its-me");

        AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        spawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () ->{
            if(self == null) return;
            if(!(self.getHealth() < maxHealth.getValue())) {
                finishedSpawning = true;
                return;
            }
            selfCreeper.setInvulnerable(true);
            selfCreeper.setAI(false);
            if((self.getHealth() + 10) > maxHealth.getValue()){
                self.setHealth(maxHealth.getValue());
            }else {
                self.setHealth(self.getHealth() + 10);
            }
            bossBar.color(BossBar.Color.values()[random.nextInt(0, BossBar.Color.values().length)]);
            self.getWorld().strikeLightning(self.getLocation().add(0, 2, 0));
            self.setFireTicks(0);
        }, 3 ,3);

        selfCreeper.setMaxFuseTicks(Integer.MAX_VALUE);
        selfCreeper.setPowered(true);
        self = selfCreeper;
        projectileDamage = 0;
        allowProjectileDamage = true;

        return true;
    }

    private void timer1M(){
        //Smite Attack
        List<Entity> nearBy = self.getNearbyEntities(5, 5, 5);
        if(!nearBy.isEmpty()){
            this.messageAllInRange("Feel my Lightning!", 20);
            for(Entity ent : nearBy){
                if(ent == self) continue;
                self.getWorld().strikeLightning(ent.getLocation());
                if(ent instanceof Player player){
                    player.sendMessage(("&4You have been Struck by Lightning!").convertToComponent());
                }
            }
        }
    }

    private void timer3S(){
        int chance = random.nextInt(0, 100);
        if(chance <= 65){
            List<Entity> nearBy = self.getNearbyEntities(20, 20, 20);
            for(Entity near : nearBy){
                if(near instanceof Player){
                    Vector vector = near.getLocation().toVector().subtract(self.getLocation().toVector()).normalize();
                    Arrow arrow = self.getWorld().spawnArrow(self.getLocation().add(0, 2, 0), vector, 1, 1);
                    arrow.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "thrown-arrow");
                }
            }
        }
    }

    private void timer15S(){
        List<Entity> close = self.getNearbyEntities(4,4,4);
        List<Entity> far = self.getNearbyEntities(15,15,15);
        for(Entity entity : far){
            if(close.contains(entity)) continue;
            Vector vector = self.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().add(new Vector(0, 2, 0));
            entity.setVelocity(vector);
        }

        this.messageAllInRange("&4There is no running away!", 15);
    }

    private void timer30S(){
        int chance = random.nextInt(0, 100);
        if(chance <= 30){
            for(int i = 0; i < random.nextInt(5, 10)+1; i++){
                Creeper minion = (Creeper) self.getWorld().spawnEntity(self.getLocation(), EntityType.CREEPER);
                minion.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 1));
                minion.setInvulnerable(true);
                minion.setMaxFuseTicks(40);
                minion.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                minion.setHealth(60);
                minion.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "minion");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () -> minion.setInvulnerable(false), 20);
            }
            this.messageAllInRange("&eGo my Minions! Show them our strength!", 30);
        }
        else if(chance <= 60){
            List<Entity> near = self.getNearbyEntities(10, 10, 10);
            if(!near.isEmpty()){
                this.messageAllInRange("&eEat my Explosives!", 30);
                for(Entity ent : near){
                    if(ent instanceof Player){
                        Vector velocity = MathUtility.calculateArc(self.getLocation().add(0, 2, 0), ent.getLocation(), 2);
                        TNTPrimed primed = (TNTPrimed) self.getWorld().spawnEntity(self.getLocation().add(0, 2, 0), EntityType.PRIMED_TNT);
                        primed.setVelocity(velocity);
                        primed.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "thrown-bomb");
                    }
                }
            }
        }
    }

    @TickTimer(ticks = 100)
    private void screwProjectileDamage(){
        if(this.projectileDamage >= 75){
            this.allowProjectileDamage = false;
            this.projectileDamage = 0;
            messageAllInRange("&4You know what... Lets disable those arrows for a little while!", 25);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, ()->allowProjectileDamage = true, 500);
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.getDrops().add(Hardcore.instance.customItemManager.getCustomItem("chargedHeart"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity().equals(self)){
            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) event.setDamage(0);
            else if(event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE)) event.setDamage(0);
            else if(event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) event.setDamage(0);
            else if(event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) event.setDamage(0);
            else if(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
                if(allowProjectileDamage)
                    this.projectileDamage += event.getDamage();
                else
                    event.setCancelled(true);
            }
        }
    }



    @EventHandler
    public void onExplosion(EntityExplodeEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof TNTPrimed || entity instanceof Creeper){
            String key = entity.getPersistentDataContainer().get(this.localKey, PersistentDataType.STRING);
            if(key == null) return;
            if(key.equals("thrown-bomb") || key.equals("minion")){
                event.blockList().clear();
            }
        }
    }

    @EventHandler
    public void onProjectile(ProjectileHitEvent event){
        if(event.getEntity() instanceof Arrow arrow){
            String key = arrow.getPersistentDataContainer().get(this.localKey, PersistentDataType.STRING);
            if(key != null && key.equals("thrown-arrow")){
                if(event.getHitEntity() != null){
                    self.getWorld().createExplosion(event.getHitEntity().getLocation(), 2f, false, false);
                }else if(event.getHitBlock() != null){
                    self.getWorld().createExplosion(event.getHitBlock().getLocation(), 2f, false, false);
                }
            }
        }
    }
}
