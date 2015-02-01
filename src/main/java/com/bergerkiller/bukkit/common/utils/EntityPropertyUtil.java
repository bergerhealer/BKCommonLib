package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.Explosion;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityHumanRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

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
        return EntityRef.chunkX.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkX(Entity entity, int chunkX) {
        EntityRef.chunkX.set(Conversion.toEntityHandle.convert(entity), chunkX);
    }

    public static int getChunkY(Entity entity) {
        return EntityRef.chunkY.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkY(Entity entity, int chunkY) {
        EntityRef.chunkY.set(Conversion.toEntityHandle.convert(entity), chunkY);
    }

    public static int getChunkZ(Entity entity) {
        return EntityRef.chunkZ.get(Conversion.toEntityHandle.convert(entity));
    }

    public static void setChunkZ(Entity entity, int chunkZ) {
        EntityRef.chunkZ.set(Conversion.toEntityHandle.convert(entity), chunkZ);
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
        DamageSource source;
        if (damager instanceof Player) {
            source = DamageSource.playerAttack(CommonNMS.getNative((Player) damager));
        } else if (damager instanceof LivingEntity) {
            source = DamageSource.mobAttack(CommonNMS.getNative((LivingEntity) damager));
        } else {
            source = DamageSource.GENERIC;
        }
        CommonNMS.getNative(entity).damageEntity(source, (float) damage);
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
        DamageSource source;
        if (cause == DamageCause.BLOCK_EXPLOSION) {
            Location loc = entity.getLocation();
            World worldhandle = CommonNMS.getNative(loc.getWorld());
            Explosion ex = new Explosion(worldhandle, null, loc.getX(), loc.getY(), loc.getZ(), (float) 4.0, false, false);
            source = DamageSource.explosion(ex);
        } else if (cause == DamageCause.CONTACT) {
            source = DamageSource.CACTUS;
        } else if (cause == DamageCause.DROWNING) {
            source = DamageSource.DROWN;
        } else if (cause == DamageCause.FALL) {
            source = DamageSource.FALL;
        } else if (cause == DamageCause.FALLING_BLOCK) {
            source = DamageSource.FALLING_BLOCK;
        } else if (cause == DamageCause.FIRE) {
            source = DamageSource.FIRE;
        } else if (cause == DamageCause.LAVA) {
            source = DamageSource.LAVA;
        } else if (cause == DamageCause.MAGIC) {
            source = DamageSource.MAGIC;
        } else if (cause == DamageCause.VOID) {
            source = DamageSource.OUT_OF_WORLD;
        } else if (cause == DamageCause.STARVATION) {
            source = DamageSource.STARVE;
        } else if (cause == DamageCause.SUFFOCATION) {
            source = DamageSource.STUCK;
        } else if (cause == DamageCause.WITHER) {
            source = DamageSource.WITHER;
        } else {
            source = DamageSource.GENERIC;
        }
        CommonNMS.getNative(entity).damageEntity(source, (float) damage);
    }

    /**
     * @deprecated use the double damage version instead
     */
    @Deprecated
    public static void damage_explode(org.bukkit.entity.Entity entity, int damage, Explosion explosion) {
        damage_explode(entity, (double) damage, explosion);
    }

    /**
     * Damages an entity with the reason of an explosion
     *
     * @param entity to be demaged
     * @param damage of the damage
     * @param explosion wich has damaged the player
     */
    public static void damage_explode(org.bukkit.entity.Entity entity, double damage, Explosion explosion) {
        CommonNMS.getNative(entity).damageEntity(DamageSource.explosion(explosion), (float) damage);
    }

    /**
     * Gets all the abilities of a human entity
     *
     * @param human to get the abilities of
     * @return human player abilities
     */
    public static PlayerAbilities getAbilities(HumanEntity human) {
        return EntityHumanRef.abilities.get(Conversion.toEntityHandle.convert(human));
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
        EntityRef.allowTeleportation.set(Conversion.toEntityHandle.convert(entity), state);
    }

    /**
     * Gets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param entity to get it for
     */
    public static boolean getAllowTeleportation(Entity entity) {
        return EntityRef.allowTeleportation.get(Conversion.toEntityHandle.convert(entity));
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
        return CommonNMS.getNative(entity).ar();
    }
}
