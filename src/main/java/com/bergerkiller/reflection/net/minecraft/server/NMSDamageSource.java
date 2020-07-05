package com.bergerkiller.reflection.net.minecraft.server;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.bergerkiller.generated.net.minecraft.server.DamageSourceHandle;

/**
 * Use DamageSourceHandle instead
 */
@Deprecated
public class NMSDamageSource {

    public static final Object FIRE = DamageSourceHandle.byName("inFire").getRaw();
    public static final Object LIGHTNING = DamageSourceHandle.byName("lightningBolt").getRaw();
    public static final Object BURN = DamageSourceHandle.byName("onFire").getRaw();
    public static final Object LAVA = DamageSourceHandle.byName("lava").getRaw();
    public static final Object STUCK = DamageSourceHandle.byName("inWall").getRaw();
    public static final Object DROWN = DamageSourceHandle.byName("drown").getRaw();
    public static final Object STARVE = DamageSourceHandle.byName("starve").getRaw();
    public static final Object CACTUS = DamageSourceHandle.byName("cactus").getRaw();
    public static final Object FALL = DamageSourceHandle.byName("fall").getRaw();
    public static final Object OUT_OF_WORLD = DamageSourceHandle.byName("outOfWorld").getRaw();
    public static final Object GENERIC = DamageSourceHandle.byName("generic").getRaw();
    public static final Object MAGIC = DamageSourceHandle.byName("magic").getRaw();
    public static final Object WITHER = DamageSourceHandle.byName("wither").getRaw();
    public static final Object ANVIL = DamageSourceHandle.byName("anvil").getRaw();
    public static final Object FALLING_BLOCK = DamageSourceHandle.byName("fallingBlock").getRaw();

    public static Object forMobAttack(LivingEntity damager) {
        return DamageSourceHandle.mobAttack(damager).getRaw();
    }

    public static Object forPlayerAttack(HumanEntity damager) {
        return DamageSourceHandle.playerAttack(damager).getRaw();
    }

    public static Object forArrowHit(Arrow arrowEntity, Entity hitEntity) {
        return DamageSourceHandle.arrowHit(arrowEntity, hitEntity).getRaw();
    }

    public static Object forThrownHit(Entity projectile, Entity hitEntity) {
        return DamageSourceHandle.thrownHit(projectile, hitEntity).getRaw();
    }

    public static Object forMagicHit(Entity source, Entity hitEntity) {
        return DamageSourceHandle.magicHit(source, hitEntity).getRaw();
    }

    public static Object forThornsDamage(Entity damagedEntity) {
        return DamageSourceHandle.thorns(damagedEntity).getRaw();
    }

    public static Entity getEntity(Object damageSource) {
        return DamageSourceHandle.createHandle(damageSource).getEntity();
    }

    public static boolean isExplosive(Object damageSource) {
        return DamageSourceHandle.createHandle(damageSource).isExplosion();
    }

    public static boolean isFireDamage(Object damageSource) {
        return DamageSourceHandle.createHandle(damageSource).isFireDamage();
    }
}
