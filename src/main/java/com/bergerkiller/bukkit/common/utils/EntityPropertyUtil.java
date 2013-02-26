package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_4_R1.DamageSource;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.EntityHumanRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

public class EntityPropertyUtil extends EntityGroupingUtil {
	private static final MethodAccessor<Void> setFirstPlayed = new SafeMethod<Void>(CommonUtil.getCBClass("entity.CraftPlayer"), "setFirstPlayed", long.class);
	private static final Material[] minecartTypes = {Material.MINECART, Material.STORAGE_MINECART, Material.POWERED_MINECART};

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

	public static void queueChunkSend(Player player, Chunk chunk) {
		queueChunkSend(player, chunk.getX(), chunk.getZ());
	}

	@SuppressWarnings("unchecked")
	public static void queueChunkSend(Player player, int chunkX, int chunkZ) {
		CommonNMS.getNative(player).chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunkX, chunkZ));
	}

	public static void cancelChunkSend(Player player, Chunk chunk) {
		cancelChunkSend(player, chunk.getX(), chunk.getZ());
	}

	public static void cancelChunkSend(Player player, int chunkX, int chunkZ) {
		CommonNMS.getNative(player).chunkCoordIntPairQueue.remove(new ChunkCoordIntPair(chunkX, chunkZ));
	}

	/**
	 * Damages an entity with as cause another entity
	 * 
	 * @param entity to be damaged
	 * @param damager that damages
	 * @param damage to deal
	 */
	public static void damageBy(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity damager, int damage) {
		DamageSource source;
		if (damager instanceof Player) {
			source = DamageSource.playerAttack(CommonNMS.getNative((Player) damager));
		} else if (damager instanceof LivingEntity) {
			source = DamageSource.mobAttack(CommonNMS.getNative((LivingEntity) damager));
		} else {
			source = DamageSource.GENERIC;
		}
		CommonNMS.getNative(entity).damageEntity(source, damage);
	}

	/**
	 * Damages an entity
	 * 
	 * @param entity to be damagedd
	 * @param cause of the damage
	 * @param damage to deal
	 */
	public static void damage(org.bukkit.entity.Entity entity, DamageCause cause, int damage) {
		DamageSource source;
		if (cause == DamageCause.BLOCK_EXPLOSION) {
			source = DamageSource.EXPLOSION;
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
		CommonNMS.getNative(entity).damageEntity(source, damage);
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
	 * Sets the first time a player played on a server or world
	 * 
	 * @param player to set it for
	 * @param firstPlayed time
	 */
	public static void setFirstPlayed(org.bukkit.entity.Player player, long firstPlayed) {
		setFirstPlayed.invoke(player, firstPlayed);
	}
}
