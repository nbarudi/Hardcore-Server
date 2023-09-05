package ca.bungo.hardcore.features.bosses.types;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.modules.utility.XRayUtility;
import ca.bungo.hardcore.types.timings.TickTimer;
import ca.bungo.hardcore.utility.ParticleUtility;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class StrangeGolemBoss extends Boss {

    private final NamespacedKey localKey;

    public StrangeGolemBoss(String name) {
        super(name);
        this.localKey = new NamespacedKey(Hardcore.instance, "strange-golem");
        this.entityName = "&#8ca6c2Strange &#c2ac8cGolem";
    }

    private boolean finishedSpawning = false;
    private boolean regenDebounce = true;
    private boolean isRegenning = false;
    private boolean triggeringSlam = false;
    private int spawnTask = 0;

    private double projectileDamage = 0;
    private boolean allowProjectileDamage = true;
    private void timer10T(){
        if(finishedSpawning) {
            Bukkit.getScheduler().cancelTask(spawnTask);
            self.setInvulnerable(false);
            self.setAI(true);
            self.setHealth(self.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            bossBar.color(BossBar.Color.PURPLE);
            regenDebounce = false;
            finishedSpawning = false;
        }
    }

    @TickTimer(ticks = 30)
    private void autoRegenTimer(){
        if(regenDebounce || triggeringSlam) return;
        isRegenning = true;
        AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(self.getHealth() < maxHealth.getValue() - 25){
            regenDebounce = true;
            self.setAI(false);
            self.setInvulnerable(true);
            ParticleBuilder builder = new ParticleBuilder(Particle.REDSTONE);
            builder.allPlayers();
            builder.color(Color.LIME);
            builder.count(50);
            builder.offset(1, 1, 1);
            builder.location(self.getLocation().add(0,1,0));
            double[] tasks = {0, 0, 0};
            Sound sound = Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.HOSTILE, 0.5f, 0.5f);
            tasks[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () -> {
                builder.location(self.getLocation().add(0, 1, 0));
                builder.spawn();
                if((self.getHealth() + 4) >= maxHealth.getValue()){
                    self.setHealth(maxHealth.getValue());
                    self.setAI(true);
                    self.setInvulnerable(false);
                    isRegenning = false;
                    cancelTask((int) tasks[0]);
                    startDebounceTimer(400);
                } else if(tasks[1] >= 75){
                    self.setAI(true);
                    self.setInvulnerable(false);
                    isRegenning = false;
                    cancelTask((int) tasks[0]);
                    startDebounceTimer(300);
                }
                else{
                    self.setHealth(self.getHealth() + 4);
                    if(tasks[2] <= 2){
                        tasks[2]+= 0.5;
                        self.teleport(self.getLocation().add(0, 0.5, 0));
                    }
                    for(Entity entity : self.getNearbyEntities(15, 15, 15)){
                        if(entity instanceof Player player){
                            player.playSound(sound);
                        }
                    }
                    tasks[1]+=4;
                }
            }, 5, 5);
        }
    }

    @TickTimer(ticks = 600)
    private void seismicSlamTimer(){
        if(isRegenning || triggeringSlam) return;
        triggeringSlam = true;
        Location first = self.getLocation().subtract(0,1,0).add(10, 0, 10);
        Location second = self.getLocation().subtract(0,1,0).subtract(10, 0, 10);
        self.setInvulnerable(true);
        self.setAI(false);
        List<Block> blockList = ParticleUtility.blocksFromTwoPoints(first, second);
        ParticleBuilder builder = new ParticleBuilder(Particle.BLOCK_DUST);
        builder.allPlayers();
        builder.count(5);
        builder.offset(0.25,0,0.25);
        int particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () -> {
            for(Block block : blockList){
                if(block == null) continue;
                builder.data(block.getBlockData());
                builder.location(block.getLocation().add(0, 1, 0));
                builder.spawn();
                Sound hitSound = Sound.sound(Key.key("block.stone.hit"),
                        Sound.Source.HOSTILE, 0.5f, 1);
                for(Entity entity : self.getNearbyEntities(10, 10, 10)){
                    if(entity instanceof Player player)
                        player.playSound(hitSound);
                }
            }
        }, 5, 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () -> {
            if(self == null) {
                Bukkit.getScheduler().cancelTask(particles);
                return;
            }
            messageAllInRange("&4Get out of my way!", 10);
            for(Entity entity : self.getNearbyEntities(10, 10, 10)){
                entity.setVelocity(entity.getVelocity().add(new Vector(0, 2, 0)).normalize());
            }
            self.setInvulnerable(false);
            self.setAI(true);
            triggeringSlam = false;
            Bukkit.getScheduler().cancelTask(particles);
        }, 100);
    }

    @TickTimer(ticks = 150)
    private void speedBoostTimer(){
        int chance = random.nextInt(0, 200);
        if(chance > 137){
            AttributeInstance attribute =  self.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if(attribute == null) return;
            attribute.setBaseValue(attribute.getValue() + 0.1);
            messageAllInRange("&5I think I should speed things along..", 20);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, () ->{
                attribute.setBaseValue(attribute.getValue()-0.1);
                messageAllInRange("&5I am getting tired...", 20);
            }, 550);
        }
    }

    @TickTimer(ticks = 100)
    private void screwProjectileDamage(){
        if(this.projectileDamage >= 35){
            this.allowProjectileDamage = false;
            this.projectileDamage = 0;
            messageAllInRange("&4You know what... Lets disable those arrows for a little while!", 25);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, ()->allowProjectileDamage = true, 500);
        }
    }

    private void startDebounceTimer(int time){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, ()->{
            regenDebounce = false;
        }, time);
    }

    @Override
    public boolean spawnSelf(EntityType type, Location location) {
        if(!super.spawnSelf(EntityType.IRON_GOLEM, location)) return false;
        IronGolem selfGolem = (IronGolem) self;

        selfGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);

        selfGolem.setInvulnerable(true);
        selfGolem.setAI(false);
        selfGolem.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "its-me");

        AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        spawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () ->{
            if(self == null) return;
            if(!(self.getHealth() < maxHealth.getValue())) {
                finishedSpawning = true;
                return;
            }
            selfGolem.setInvulnerable(true);
            selfGolem.setAI(false);
            if((self.getHealth() + 10) > maxHealth.getValue()){
                self.setHealth(maxHealth.getValue());
            }else {
                self.setHealth(self.getHealth() + 10);
            }
            bossBar.color(BossBar.Color.values()[random.nextInt(0, BossBar.Color.values().length)]);
            //ToDo: Spawn Effect
        }, 3 ,3);

        self = selfGolem;
        projectileDamage = 0;
        allowProjectileDamage = true;
        return true;
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onHurt(EntityDamageEvent event){
        if(event.getEntity() == self){
            if(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
                if(allowProjectileDamage)
                    projectileDamage += event.getDamage();
                else
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamagingOther(EntityDamageByEntityEvent event){
        if(event.getDamager() == self){
            Entity entity = event.getEntity();
            if(entity instanceof Player player){
                if(player.isBlocking()){
                    player.setCooldown(Material.SHIELD, 200);
                }
                player.setVelocity(new Vector(0,1,0));
            }
        }
    }


}
