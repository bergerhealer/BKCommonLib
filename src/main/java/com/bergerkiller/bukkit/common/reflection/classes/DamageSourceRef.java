package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityArrow;
import net.minecraft.server.v1_8_R1.EntityFireball;
import net.minecraft.server.v1_8_R1.Explosion;

public class DamageSourceRef {

    public static final Object FIRE = DamageSource.FIRE;
    public static final Object LAVA = DamageSource.LAVA;
    public static final Object STUCK = DamageSource.STUCK;
    public static final Object DROWN = DamageSource.DROWN;
    public static final Object STARVE = DamageSource.STARVE;
    public static final Object CACTUS = DamageSource.CACTUS;
    public static final Object FALL = DamageSource.FALL;
    public static final Object OUT_OF_WORLD = DamageSource.OUT_OF_WORLD;
    public static final Object GENERIC = DamageSource.GENERIC;
    public static final Object MAGIC = DamageSource.MAGIC;
    public static final Object WITHER = DamageSource.WITHER;
    public static final Object ANVIL = DamageSource.ANVIL;
    public static final Object FALLING_BLOCK = DamageSource.FALLING_BLOCK;

    public static Object forMobAttack(LivingEntity damager) {
        return DamageSource.mobAttack(CommonNMS.getNative(damager));
    }

    public static Object forPlayerAttack(HumanEntity damager) {
        return DamageSource.playerAttack(CommonNMS.getNative(damager));
    }

    public static Object forArrowHit(Arrow arrowEntity, Entity hitEntity) {
        return DamageSource.arrow(CommonNMS.getNative(arrowEntity, EntityArrow.class), CommonNMS.getNative(hitEntity));
    }

    public static Object forFireballHit(Fireball fireballEntity, Entity hitEntity) {
        return DamageSource.fireball(CommonNMS.getNative(fireballEntity, EntityFireball.class), CommonNMS.getNative(hitEntity));
    }

    public static Object forThrownHit(Entity projectile, Entity hitEntity) {
        return DamageSource.projectile(CommonNMS.getNative(projectile), CommonNMS.getNative(hitEntity));
    }

    public static Object forMagicHit(Entity source, Entity hitEntity) {
        return DamageSource.b(CommonNMS.getNative(source), CommonNMS.getNative(hitEntity));
    }

    public static Object forThornsDamage(Entity damagedEntity) {
        return DamageSource.a(CommonNMS.getNative(damagedEntity));
    }

    public static Object forExplosion(Entity explodableEntity) {
        Explosion expl = null;
        if (explodableEntity != null) {
            expl = new Explosion(null, CommonNMS.getNative(explodableEntity), 0.0, 0.0, 0.0, 0.0f, false, false);
            // new Explosion(null, CommonNMS.getNative(explodableEntity), 0.0, 0.0, 0.0, 0.0f); Look deeper into
        }
        return DamageSource.explosion(expl);
    }

    public static Entity getEntity(Object damageSource) {
        return CommonNMS.getEntity(((DamageSource) damageSource).getEntity());
    }

    public static boolean isExplosive(Object damageSource) {
        return ((DamageSource) damageSource).isExplosion();
    }

    public static boolean isFireDamage(Object damageSource) {
        return ((DamageSource) damageSource).o();
    }
}
