package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityHuman;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityPropertyUtil extends EntityGroupingUtil {

    private static final Material[] minecartTypes = {Material.MINECART, Material.STORAGE_MINECART, Material.POWERED_MINECART,
        Material.EXPLOSIVE_MINECART, Material.HOPPER_MINECART};

    /**
     * Gets all available types of Minecarts as item materials
     *
     * @return minecart types
     */
    public static Material[] getMinecartTypes() {
        return minecartTypes;
    }

    public static double getLocX(Entity entity) {
        return CommonNMS.getNative(entity).locX;
    }

    public static void setLocX(Entity entity, double value) {
        CommonNMS.getNative(entity).locX = value;
    }

    public static double getLocY(Entity entity) {
        return CommonNMS.getNative(entity).locY;
    }

    public static void setLocY(Entity entity, double value) {
        CommonNMS.getNative(entity).locY = value;
    }

    public static double getLocZ(Entity entity) {
        return CommonNMS.getNative(entity).locZ;
    }

    public static void setLocZ(Entity entity, double value) {
        CommonNMS.getNative(entity).locZ = value;
    }

    public static double getMotX(Entity entity) {
        return CommonNMS.getNative(entity).motX;
    }

    public static void setMotX(Entity entity, double value) {
        CommonNMS.getNative(entity).motX = value;
    }

    public static double getMotY(Entity entity) {
        return CommonNMS.getNative(entity).motY;
    }

    public static void setMotY(Entity entity, double value) {
        CommonNMS.getNative(entity).motY = value;
    }

    public static double getMotZ(Entity entity) {
        return CommonNMS.getNative(entity).motZ;
    }

    public static void setMotZ(Entity entity, double value) {
        CommonNMS.getNative(entity).motZ = value;
    }

    public static double getLastX(Entity entity) {
        return CommonNMS.getNative(entity).lastX;
    }

    public static void setLastX(Entity entity, double value) {
        CommonNMS.getNative(entity).lastX = value;
    }

    public static double getLastY(Entity entity) {
        return CommonNMS.getNative(entity).lastY;
    }

    public static void setLastY(Entity entity, double value) {
        CommonNMS.getNative(entity).lastY = value;
    }

    public static double getLastZ(Entity entity) {
        return CommonNMS.getNative(entity).lastZ;
    }

    public static void setLastZ(Entity entity, double value) {
        CommonNMS.getNative(entity).lastZ = value;
    }

    public static int getChunkX(Entity entity) {
        return NMSEntity.chunkX.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkX(Entity entity, int chunkX) {
        NMSEntity.chunkX.set(Conversion.toEntityHandle.convert(entity), chunkX);
    }

    public static int getChunkY(Entity entity) {
        return NMSEntity.chunkY.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkY(Entity entity, int chunkY) {
        NMSEntity.chunkY.set(Conversion.toEntityHandle.convert(entity), chunkY);
    }

    public static int getChunkZ(Entity entity) {
        return NMSEntity.chunkZ.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkZ(Entity entity, int chunkZ) {
        NMSEntity.chunkZ.set(Conversion.toEntityHandle.convert(entity), chunkZ);
    }

    public static void setDead(Entity entity, boolean dead) {
        CommonNMS.getNative(entity).dead = dead;
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
        if (cause == DamageCause.BLOCK_EXPLOSION) {
        	CommonNMS.getNative(entity).damageEntity(CommonMethods.DamageSource_explosion(entity,  cause, damage), (float) damage);
        } else {
        	CommonNMS.getNative(entity).damageEntity(CommonMethods.DamageSource_from_damagecause(cause), (float) damage);
        }
    }

    /**
     * Gets all the abilities of a human entity
     *
     * @param human to get the abilities of
     * @return human player abilities
     */
    public static PlayerAbilities getAbilities(HumanEntity human) {
        return NMSEntityHuman.abilities.get(Conversion.toEntityHandle.convert(human));
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
        NMSEntity.allowTeleportation.set(Conversion.toEntityHandle.convert(entity), state);
    }

    /**
     * Gets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param entity to get it for
     */
    public static boolean getAllowTeleportation(Entity entity) {
        return NMSEntity.allowTeleportation.get(Conversion.toEntityHandle.convert(entity));
    }

    /**
     * Sets the entity portal enter cooldown ticks
     *
     * @param entity to set it for
     * @param cooldownTicks to set to
     */
    public static void setPortalCooldown(Entity entity, int cooldownTicks) {
        CommonNMS.getNative(entity).portalCooldown = cooldownTicks;
    }

    /**
     * Gets the entity portal enter cooldown ticks
     *
     * @param entity to get it for
     * @return entity cooldown ticks
     */
    public static int getPortalCooldown(Entity entity) {
        return CommonNMS.getNative(entity).portalCooldown;
    }

    /**
     * Gets the maximum portal cooldown ticks. This is the value applied right
     * after entering a portal.
     *
     * @param entity to get it for
     * @return entity maximum portal cooldown ticks
     */
    public static int getPortalCooldownMaximum(Entity entity) {
    	//TODO: Broken!!!!
    	return 0;
        //return CommonNMS.getNative(entity).aC();
    }
}
