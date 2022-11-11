package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityPropertyUtil extends EntityGroupingUtil {
    private static final Material[] minecartTypes = MaterialUtil.ISMINECART.getMaterials().toArray(new Material[0]);

    /**
     * Gets all available types of Minecarts as item materials
     *
     * @return minecart types
     */
    public static Material[] getMinecartTypes() {
        return minecartTypes;
    }

    private static final Object h(org.bukkit.entity.Entity entity) {
        return HandleConversion.toEntityHandle(entity);
    }

    @Deprecated
    public static double getLocX(Entity entity) {
        return EntityHandle.T.getLocX.invoke(h(entity));
    }

    @Deprecated
    public static void setLocX(Entity entity, double value) {
        EntityHandle.T.setLocX.invoke(h(entity), value);
    }

    @Deprecated
    public static double getLocY(Entity entity) {
        return EntityHandle.T.getLocY.invoke(h(entity));
    }

    @Deprecated
    public static void setLocY(Entity entity, double value) {
        EntityHandle.T.setLocY.invoke(h(entity), value);
    }

    @Deprecated
    public static double getLocZ(Entity entity) {
        return EntityHandle.T.getLocZ.invoke(h(entity));
    }

    @Deprecated
    public static void setLocZ(Entity entity, double value) {
        EntityHandle.T.setLocZ.invoke(h(entity), value);
    }

    @Deprecated
    public static double getMotX(Entity entity) {
        return EntityHandle.T.getMotX.invoke(h(entity));
    }

    @Deprecated
    public static void setMotX(Entity entity, double value) {
        EntityHandle.T.setMotX.invoke(h(entity), value);
    }

    @Deprecated
    public static double getMotY(Entity entity) {
        return EntityHandle.T.getMotY.invoke(h(entity));
    }

    @Deprecated
    public static void setMotY(Entity entity, double value) {
        EntityHandle.T.setMotY.invoke(h(entity), value);
    }

    @Deprecated
    public static double getMotZ(Entity entity) {
        return EntityHandle.T.getMotZ.invoke(h(entity));
    }

    @Deprecated
    public static void setMotZ(Entity entity, double value) {
        EntityHandle.T.setMotZ.invoke(h(entity), value);
    }

    public static double getLastX(Entity entity) {
        return EntityHandle.T.lastX.getDouble(h(entity));
    }

    public static void setLastX(Entity entity, double value) {
        EntityHandle.T.lastX.setDouble(h(entity), value);
    }

    public static double getLastY(Entity entity) {
        return EntityHandle.T.lastY.getDouble(h(entity));
    }

    public static void setLastY(Entity entity, double value) {
        EntityHandle.T.lastY.setDouble(h(entity), value);
    }

    public static double getLastZ(Entity entity) {
        return EntityHandle.T.lastZ.getDouble(h(entity));
    }

    public static void setLastZ(Entity entity, double value) {
        EntityHandle.T.lastZ.setDouble(h(entity), value);
    }

    public static int getChunkX(Entity entity) {
        return EntityHandle.T.getChunkX.invoke(h(entity));
    }

    public static int getChunkY(Entity entity) {
        return EntityHandle.T.getChunkY.invoke(h(entity));
    }

    public static int getChunkZ(Entity entity) {
        return EntityHandle.T.getChunkZ.invoke(h(entity));
    }

    /**
     * @deprecated Use {@link #setDestroyed(Entity, boolean)} instead
     */
    @Deprecated
    public static void setDead(Entity entity, boolean dead) {
        EntityHandle.T.setDestroyed.invoke(h(entity), dead);
    }

    public static void setDestroyed(Entity entity, boolean dead) {
        EntityHandle.T.setDestroyed.invoke(h(entity), dead);
    }

    /**
     * @deprecated Use double damage version instead
     */
    @Deprecated
    public static void damageBy(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity damager, int damage) {
        damageBy(entity, damager, (double) damage);
    }

    /**
     * Damages an entity with as cause another entity
     *
     * @param entity to be damaged
     * @param damager that damages
     * @param damage to deal
     */
    public static void damageBy(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity damager, double damage) {
        CommonMethods.damageBy(entity, damager, damage);
    }

    /**
     * @deprecated Use double damage version instead
     */
    @Deprecated
    public static void damage(org.bukkit.entity.Entity entity, DamageCause cause, int damage) {
        damage(entity, cause, (double) damage);
    }

    /**
     * Damages an entity
     *
     * @param entity to be damagedd
     * @param cause of the damage
     * @param damage to deal
     */
    public static void damage(org.bukkit.entity.Entity entity, DamageCause cause, double damage) {
        EntityHandle eh = EntityHandle.fromBukkit(entity);
        eh.damageEntity(CommonMethods.DamageSource_from_damagecause(cause), (float) damage);
    }

    /**
     * Gets all the abilities of a human entity
     *
     * @param human to get the abilities of
     * @return human player abilities
     */
    public static PlayerAbilities getAbilities(HumanEntity human) {
        return EntityHumanHandle.T.abilities.get(HandleConversion.toEntityHandle(human));
    }

    /**
     * Sets the invulerability state of an Entity
     *
     * @param entity to set it for
     * @param state to set to
     */
    public static void setInvulnerable(Entity entity, boolean state) {
        if (entity instanceof HumanEntity) {
            getAbilities((HumanEntity) entity).setInvulnerable(state);
        }
    }

    /**
     * Gets the invulerability state of an Entity
     *
     * @param entity to get it for
     * @return invulnerability state
     */
    public static boolean isInvulnerable(org.bukkit.entity.Entity entity) {
        if (entity instanceof HumanEntity) {
            return getAbilities((HumanEntity) entity).isInvulnerable();
        }
        return false;
    }

    /**
     * Sets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param entity to set it for
     * @param state to set to
     */
    public static void setAllowTeleportation(Entity entity, boolean state) {
        EntityHandle.T.allowTeleportation.setBoolean(h(entity), state);
    }

    /**
     * Gets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param entity to get it for
     */
    public static boolean getAllowTeleportation(Entity entity) {
        return EntityHandle.T.allowTeleportation.getBoolean(h(entity));
    }

    /**
     * Sets the entity portal enter cooldown ticks
     *
     * @param entity to set it for
     * @param cooldownTicks to set to
     */
    public static void setPortalCooldown(Entity entity, int cooldownTicks) {
        EntityHandle.T.portalCooldown.setInteger(h(entity), cooldownTicks);
    }

    /**
     * Gets the entity portal enter cooldown ticks
     *
     * @param entity to get it for
     * @return entity cooldown ticks
     */
    public static int getPortalCooldown(Entity entity) {
        return EntityHandle.T.portalCooldown.getInteger(h(entity));
    }

    /**
     * Gets the maximum portal cooldown ticks. This is the value applied right
     * after entering a portal.
     *
     * @param entity to get it for
     * @return entity maximum portal cooldown ticks
     */
    public static int getPortalCooldownMaximum(Entity entity) {
        return EntityHandle.T.getPortalCooldownMaximum.invoke(h(entity));
    }

    /**
     * Sets the number of ticks an entity spent inside a portal.
     * This is used for a portal teleport delay.
     *
     * @param entity to set it for
     * @param timeTicks Number of ticks to set to
     */
    public static void setPortalTime(Entity entity, int timeTicks) {
        EntityHandle.T.portalTime.setInteger(h(entity), timeTicks);
    }

    /**
     * Gets the number of ticks an entity spent inside a portal.
     * This is used for a portal teleport delay.
     *
     * @param entity to get it for
     * @return entity portal time duration
     */
    public static int getPortalTime(Entity entity) {
        return EntityHandle.T.portalTime.getInteger(h(entity));
    }

    /**
     * Gets the number of ticks an Entity must be inside a portal before
     * the portal teleports the entity to the other end.
     *
     * @param entity to get it for
     * @return portal wait time
     */
    public static int getPortalWaitTime(Entity entity) {
        return EntityHandle.T.getPortalWaitTime.invoke(h(entity));
    }
}
