package ca.bungo.hardcore.features.bosses.types;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.types.timings.TickTimer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int spawnTask;

    private final Sound bossSound = Sound.sound(Key.key("custom.traveler"), Sound.Source.MUSIC, 1f, 1);

    public BardgoBoss(String name) {
        super(name);
        this.entityName = "&eT&ch&ae &bT&5r&7a&bv&1e&2l&3e&4r";
        this.localKey = new NamespacedKey(Hardcore.instance, "bardgo");
        this.damageCause = new HashMap<>();
    }

    boolean finishedSpawning = false;

    @TickTimer(ticks=10)
    private void waitingForSpawn(){
        if(finishedSpawning) {
            Bukkit.getScheduler().cancelTask(spawnTask);
            self.setInvulnerable(false);
            self.setAI(true);
            self.setHealth(self.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            bossBar.color(BossBar.Color.RED);
            finishedSpawning = false;
        }
    }

    @Override
    public boolean spawnSelf(EntityType type, Location location) {
        //if(!super.spawnSelf(EntityType.GIANT, location)) return false;

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

    @Override
    public void onDeath(EntityDeathEvent event) {
        event.getEntity().getWorld().stopSound(bossSound);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity().equals(self)){
            EntityDamageEvent.DamageCause cause = event.getCause();
            double damage = 0;

            if(damageCause.containsKey(cause))
                damage = damageCause.get(cause);

            if(damage >= 45) event.setCancelled(true);
            else{
                damageCause.put(cause, damage+event.getDamage());
                if(damage+event.getDamage() >= 45){
                    messageAllInRange("&4You know what... Lets see how well you can adapt to this!", 30);
                    Bukkit.getScheduler().runTaskLater(Hardcore.instance, () -> damageCause.put(cause, 0.0D), 300);
                }
            }
        }
    }
}
