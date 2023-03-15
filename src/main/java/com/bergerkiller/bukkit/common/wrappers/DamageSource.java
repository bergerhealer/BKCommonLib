package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;

import org.bukkit.entity.Entity;

public class DamageSource extends BasicWrapper<DamageSourceHandle> {

    @Deprecated
    protected DamageSource(Object damageSource) {
        setHandle(DamageSourceHandle.createHandle(damageSource));
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

    @Override
    public String toString() {
        return handle.getTranslationIndex();
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
        return new DamageSource(damageSource);
    }
}
