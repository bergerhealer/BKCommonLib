package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.DamageSourceHandle;

import org.bukkit.entity.Entity;

public class DamageSource extends BasicWrapper<DamageSourceHandle> {

    public static final DamageSource FIRE = new DamageSource("inFire");
    public static final DamageSource LIGHTNING = new DamageSource("lightningBolt");
    public static final DamageSource BURN = new DamageSource("onFire");
    public static final DamageSource LAVA = new DamageSource("lava");
    public static final DamageSource STUCK = new DamageSource("inWall");
    public static final DamageSource DROWN = new DamageSource("drown");
    public static final DamageSource STARVE = new DamageSource("starve");
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = new DamageSource("fall");
    public static final DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld");
    public static final DamageSource GENERIC = new DamageSource("generic");
    public static final DamageSource MAGIC = new DamageSource("magic");
    public static final DamageSource WITHER = new DamageSource("wither");
    public static final DamageSource ANVIL = new DamageSource("anvil");
    public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
    private static final DamageSource[] values = CommonUtil.getClassConstants(DamageSource.class);

    @Deprecated
    protected DamageSource(Object damageSource) {
        setHandle(DamageSourceHandle.createHandle(damageSource));
    }

    protected DamageSource(String name) {
        setHandle(DamageSourceHandle.byName(name));
    }

    protected DamageSource(DamageSourceHandle damageSourceHandle) {
        setHandle(damageSourceHandle);
    }

    public boolean isFireDamage() {
        return handle.isFireDamage();
    }

    public boolean isExplosive() {
        return handle.isExplosion();
    }

    /**
     * Gets the Entity that dealt the damage
     *
     * @return the Damager Entity, or null if there is none
     */
    public Entity getEntity() {
        return handle.getEntity();
    }

    /**
     * Obtains the DamageSource wrapper Class for a given DamageSource handle
     *
     * @param damageSource handle
     * @return DamageSource wrapper for the handle
     */
    public static DamageSource getForHandle(Object damageSource) {
        for (DamageSource value : values) {
            if (value.handle.getRaw() == damageSource) {
                return value;
            }
        }
        // Try to obtain it generic
        // TODO: Unique damage source types?
        return new DamageSource(damageSource);
    }
}
