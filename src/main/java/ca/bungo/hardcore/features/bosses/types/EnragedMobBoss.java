package ca.bungo.hardcore.features.bosses.types;

import ca.bungo.hardcore.features.bosses.Boss;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftAnimals;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnragedMobBoss extends Boss {


    private static class CustomNearestAttackableTarget<T extends net.minecraft.world.entity.LivingEntity>
            extends NearestAttackableTargetGoal<T> {

        public CustomNearestAttackableTarget(Mob mob, Class<T> targetClass, boolean checkVisibility) {
            super(mob, targetClass, checkVisibility);
            this.targetConditions.useFollowRange();
        }
    }

    public EnragedMobBoss(String name) {
        super(name);
    }

    @Override
    public boolean spawnSelf(EntityType type, Location location) {
        if(type == null) type = EntityType.PIG;
        String translationKey = type.translationKey();
        String[] logic = translationKey.split("\\.");
        translationKey = logic[logic.length-1].replace('_', ' ');
        String name = WordUtils.capitalize(translationKey);

        this.entityName = "&4Enraged &e" + name;
        if(!super.spawnSelf(type, location)) return false;
        Animal animal = ((CraftAnimals) self).getHandle();
        animal.getAttributes().registerAttribute(Attributes.ATTACK_DAMAGE);

        animal.goalSelector.addGoal(0, new MeleeAttackGoal(animal, 2.0f, false));
        animal.targetSelector.addGoal(0, new CustomNearestAttackableTarget<>(animal, Player.class, true));

        animal.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0f);
        animal.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(128.0f);

        self.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        self.setHealth(50);
        self.getWorld().playSound(self.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1);

        return true;
    }

    @Override
    public void onDeath(EntityDeathEvent event) {

    }
}
