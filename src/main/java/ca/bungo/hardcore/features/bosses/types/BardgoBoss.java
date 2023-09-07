package ca.bungo.hardcore.features.bosses.types;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.types.timings.TickTimer;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BardgoBoss extends Boss {

    private static class CustomGiantEntity extends Giant {
        private static class GiantAttackGoal extends MeleeAttackGoal {
            private int raiseArmTicks;
            private final Giant zombie;

            public GiantAttackGoal(Giant zombie, double speed, boolean pauseWhenMobIdle) {
                super(zombie, speed, pauseWhenMobIdle);
                this.zombie = zombie;
            }

            @Override
            public void start() {
                super.start();
                this.raiseArmTicks = 0;
            }

            @Override
            public void stop() {
                super.stop();
                this.zombie.setAggressive(false);
            }

            @Override
            public void tick() {
                super.tick();
                ++this.raiseArmTicks;
                this.zombie.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2);

            }
        }
        private static class CustomNearestAttackableTarget<T extends net.minecraft.world.entity.LivingEntity>
                extends NearestAttackableTargetGoal<T> {

            public CustomNearestAttackableTarget(Mob mob, Class<T> targetClass, boolean checkVisibility) {
                super(mob, targetClass, checkVisibility);
                this.targetConditions.useFollowRange();
            }
        }

        public CustomGiantEntity(World world) {
            super(net.minecraft.world.entity.EntityType.GIANT, ((CraftWorld)world).getHandle());
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(128.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            this.goalSelector.addGoal(2, new GiantAttackGoal(this, 1.0D, false));
            this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5D));
            this.targetSelector.addGoal(2, new CustomNearestAttackableTarget<>(this, Player.class, true));

            super.registerGoals();
        }
    }


    private final NamespacedKey localKey;
    private final Map<EntityDamageEvent.DamageCause, Double> damageCause;
    private final Map<EntityDamageEvent.DamageCause, Double> damageRecord;
    private int spawnTask;

    private final Sound bossSound = Sound.sound(Key.key("custom.traveler"), Sound.Source.HOSTILE, 0.2f, 1);
    private final Sound bossSound2 = Sound.sound(Key.key("custom.traveler.stagetwo"), Sound.Source.HOSTILE, 0.2f, 1f);
    private final Sound bossVoice = Sound.sound(Key.key("custom.traveler.immunity"), Sound.Source.HOSTILE, 2, 1);

    public BardgoBoss(String name) {
        super(name);
        this.entityName = "&bT&5r&7a&bv&1e&2l&3e&4r";
        this.localKey = new NamespacedKey(Hardcore.instance, "bardgo");
        this.damageCause = new HashMap<>();
        this.damageRecord = new HashMap<>();
    }

    boolean finishedSpawning = false;
    boolean secondStage = false;
    private boolean regenDebounce = true;
    private boolean isRegenning = false;
    boolean secondStageStarting = false;

    @TickTimer(ticks=10)
    private void waitingForSpawn(){
        if(finishedSpawning) {
            Bukkit.getScheduler().cancelTask(spawnTask);
            self.setInvulnerable(false);
            self.setAI(true);
            self.setHealth(self.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            bossBar.color(BossBar.Color.RED);
            regenDebounce = false;
            finishedSpawning = false;
        }
    }

    @Override
    public boolean spawnSelf(EntityType type, Location location) {
        if(!this.canSpawn()) return false;
        CustomGiantEntity giant = new CustomGiantEntity(location.getWorld());
        self = (LivingEntity) giant.getBukkitEntity();
        self.spawnAt(location);

        if(!this.entityName.isEmpty()){
            self.setCustomNameVisible(true);
            self.customName(this.entityName.convertToComponent());
        }
        bossBar = BossBar.bossBar(this.entityName.convertToComponent(), 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        bossBar.addFlag(BossBar.Flag.PLAY_BOSS_MUSIC);
        bossBar.addFlag(BossBar.Flag.DARKEN_SCREEN);
        finishedSpawning = false;

        self.setHealth(1);

        self.getWorld().playSound(Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.MASTER, 1, 1));
        self.getWorld().playSound(bossSound);

        self.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
        self.setInvulnerable(true);
        self.setAI(false);
        self.getPersistentDataContainer().set(this.localKey, PersistentDataType.STRING, "its-me");

        AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        spawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () ->{
            if(self == null) return;
            if(!(self.getHealth() < maxHealth.getValue())) {
                finishedSpawning = true;
                return;
            }
            self.setInvulnerable(true);
            self.setAI(false);
            if((self.getHealth() + 4) > maxHealth.getValue()){
                self.setHealth(maxHealth.getValue());
            }else {
                self.setHealth(self.getHealth() + 4);
            }
            bossBar.color(BossBar.Color.values()[random.nextInt(0, BossBar.Color.values().length)]);

            //ToDo: Spawn Effect

        }, 3 ,3);

        this.damageCause.clear();
        this.damageRecord.clear();
        secondStage = false;
        return true;
    }

    @TickTimer
    private void stepTimer(){
        List<Entity> near = self.getNearbyEntities(2, 2, 2);
        for(Entity ent : near){
            if(ent instanceof org.bukkit.entity.Player player){
                if(player.getLocation().distance(self.getLocation()) < 3.0f){
                    if(player.isDead() || !player.getGameMode().equals(GameMode.SURVIVAL)) continue;
                    player.damage(1000.0D, self);
                    Bukkit.broadcast((player.getName() + " &ewas stepped on!").convertToComponent());
                }
            }
        }
    }

    @TickTimer(ticks = 400)
    private void smiteAttack(){
        if(!secondStage) return;
        //Smite Attack
        List<Entity> nearBy = self.getNearbyEntities(10, 10, 10);
        if(!nearBy.isEmpty()){
            this.messageAllInRange("&rFeel my Lightning!", 20);
            for(Entity ent : nearBy){
                if(ent == self) continue;
                self.getWorld().strikeLightning(ent.getLocation());
                if(ent instanceof org.bukkit.entity.Player player){
                    player.sendMessage(("&4You have been Struck by Lightning!").convertToComponent());
                }
            }
        }
    }

    @TickTimer(ticks = 120)
    private void autoRegenTimer(){
        if(!secondStage || secondStageStarting) return;
        if(regenDebounce) return;
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
            self.getWorld().playSound(bossVoice);
            tasks[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () -> {
                builder.location(self.getLocation().add(0, 1, 0));
                builder.spawn();
                if((self.getHealth() + 4) >= maxHealth.getValue()){
                    self.setHealth(maxHealth.getValue());
                    self.setAI(true);
                    self.setInvulnerable(false);
                    isRegenning = false;
                    cancelTask((int) tasks[0]);
                    startDebounceTimer(700);
                } else if(tasks[1] >= 75){
                    self.setAI(true);
                    self.setInvulnerable(false);
                    isRegenning = false;
                    cancelTask((int) tasks[0]);
                    startDebounceTimer(700);
                }
                else{
                    self.setHealth(self.getHealth() + 4);
                    if(tasks[2] <= 2){
                        tasks[2]+= 0.5;
                        self.teleport(self.getLocation().add(0, 0.5, 0));
                    }
                    for(Entity entity : self.getNearbyEntities(15, 15, 15)){
                        if(entity instanceof org.bukkit.entity.Player player){
                            //player.playSound(sound);
                        }
                    }
                    tasks[1]+=4;
                }
            }, 5, 5);
        }
    }


    @Override
    public void onDeath(EntityDeathEvent event) {
        if(!secondStage){
            secondStageStarting = true;
            event.getEntity().getWorld().stopSound(bossSound);
            event.getEntity().getWorld().stopSound(bossSound2);
            event.getEntity().getWorld().stopSound(bossVoice);
            event.getEntity().getWorld().playSound(Sound.sound(Key.key("custom.traveler.death.fake"), Sound.Source.HOSTILE, 2, 1));
            event.getEntity().getWorld().stopSound(bossVoice);
            secondStage = true;

            event.setCancelled(true);
            self = event.getEntity();
            AttributeInstance maxHealth = self.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            maxHealth.setBaseValue(800.0D);
            self.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4D);
            self.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40.0D);
            this.finishedSpawning = false;

            self.setAI(false);
            self.setInvulnerable(true);

            spawnTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Hardcore.instance, () ->{
                if(self == null) return;
                if(!(self.getHealth() < maxHealth.getValue())) {
                    finishedSpawning = true;
                    event.getEntity().getWorld().stopSound(bossSound2);
                    event.getEntity().getWorld().playSound(bossSound2);
                    secondStageStarting = false;
                    return;
                }
                self.setInvulnerable(true);
                self.setAI(false);
                if((self.getHealth() + 4) > maxHealth.getValue()){
                    self.setHealth(maxHealth.getValue());
                }else {
                    self.setHealth(self.getHealth() + 4);
                }
                bossBar.color(BossBar.Color.values()[random.nextInt(0, BossBar.Color.values().length)]);
                self.getWorld().strikeLightningEffect(self.getLocation().add(new Random().nextInt(-3, 3), 3,
                        new Random().nextInt(-3, 3)));
                self.getWorld().stopSound(SoundStop.named(Key.key("entity.lightning_bolt.thunder")));
                self.getWorld().stopSound(SoundStop.named(Key.key("entity.lightning_bolt.impact")));
            }, 1 ,1);

        }else{
            event.getEntity().getWorld().stopSound(bossVoice);
            event.getEntity().getWorld().stopSound(bossSound2);
            event.getEntity().getWorld().playSound(Sound.sound(Key.key("custom.traveler.death"), Sound.Source.HOSTILE, 2, 1));
            event.getEntity().getWorld().stopSound(bossVoice);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity().equals(self)){
            EntityDamageEvent.DamageCause cause = event.getCause();
            double damage = 0;
            if(damageCause.containsKey(cause))
                damage = damageCause.get(cause);
            if(damage >= 45){
                event.setCancelled(true);

                if(secondStage && !secondStageStarting){
                    if(damageRecord.containsKey(cause))
                        damageRecord.put(cause, damageRecord.get(cause) + event.getDamage());
                    else
                        damageRecord.put(cause, event.getDamage());
                }
            }
            else{
                damageCause.put(cause, damage+event.getDamage());
                if(damage+event.getDamage() >= 45){
                    messageAllInRange("&4You know what... Lets see how well you can adapt to this!", 30);
                    if(!secondStageStarting)
                        self.getWorld().playSound(bossVoice);
                    Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> {
                        damageCause.put(cause, 0.0D);
                        if(secondStage && !secondStageStarting){
                            List<org.bukkit.entity.Player> nearPlayers = new ArrayList<>();
                            if(self == null) return;
                            for(Entity entity : self.getNearbyEntities(30, 30, 30)){
                                if(entity instanceof org.bukkit.entity.Player player)
                                    nearPlayers.add(player);
                            }

                            if(!damageRecord.containsKey(cause)) return;

                            double totalDamage = damageRecord.get(cause);
                            if(nearPlayers.isEmpty()) return;

                            double dmg = totalDamage/ nearPlayers.size();
                            for(org.bukkit.entity.Player player : nearPlayers){
                                player.damage(dmg);
                                damageRecord.remove(cause);
                            }
                            messageAllInRange("How do you like it!", 30);
                        }
                    }, 300);
                }
            }

        }
    }

    private void startDebounceTimer(int time){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.instance, ()->{
            regenDebounce = false;
        }, time);
    }
}
