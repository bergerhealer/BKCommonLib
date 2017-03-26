package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSDamageSource;

import org.bukkit.entity.Entity;

public class DamageSource extends BasicWrapper {

    public static final DamageSource FIRE = new DamageSource(NMSDamageSource.FIRE);
    public static final DamageSource LIGHTNING = new DamageSource(NMSDamageSource.LIGHTNING);
    public static final DamageSource BURN = new DamageSource(NMSDamageSource.BURN);
    public static final DamageSource LAVA = new DamageSource(NMSDamageSource.LAVA);
    public static final DamageSource STUCK = new DamageSource(NMSDamageSource.STUCK);
    public static final DamageSource DROWN = new DamageSource(NMSDamageSource.DROWN);
    public static final DamageSource STARVE = new DamageSource(NMSDamageSource.STARVE);
    public static final DamageSource CACTUS = new DamageSource(NMSDamageSource.CACTUS);
    public static final DamageSource FALL = new DamageSource(NMSDamageSource.FALL);
    public static final DamageSource OUT_OF_WORLD = new DamageSource(NMSDamageSource.OUT_OF_WORLD);
    public static final DamageSource GENERIC = new DamageSource(NMSDamageSource.GENERIC);
    public static final DamageSource MAGIC = new DamageSource(NMSDamageSource.MAGIC);
    public static final DamageSource WITHER = new DamageSource(NMSDamageSource.WITHER);
    public static final DamageSource ANVIL = new DamageSource(NMSDamageSource.ANVIL);
    public static final DamageSource FALLING_BLOCK = new DamageSource(NMSDamageSource.FALLING_BLOCK);
    private static final DamageSource[] values = CommonUtil.getClassConstants(DamageSource.class);

    protected DamageSource(Object damageSource) {
        setHandle(damageSource);
    }

    public boolean isFireDamage() {
        return NMSDamageSource.isFireDamage(handle);
    }

    public boolean isExplosive() {
        return NMSDamageSource.isExplosive(handle);
    }

    /**
     * Gets the Entity that dealt the damage
     *
     * @return the Damager Entity, or null if there is none
     */
    public Entity getEntity() {
        return NMSDamageSource.getEntity(handle);
    }

    /**
     * Obtains the DamageSource wrapper Class for a given DamageSource handle
     *
     * @param damageSource handle
     * @return DamageSource wrapper for the handle
     */
    public static DamageSource getForHandle(Object damageSource) {
        for (DamageSource value : values) {
            if (value.handle == damageSource) {
                return value;
            }
        }
        // Try to obtain it generic
        // TODO: Unique damage source types?
        return new DamageSource(damageSource);
    }
}
