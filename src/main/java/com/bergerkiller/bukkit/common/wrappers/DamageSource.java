package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.reflection.classes.DamageSourceRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class DamageSource extends BasicWrapper {
	public static final DamageSource FIRE = new DamageSource(DamageSourceRef.FIRE);
	public static final DamageSource LAVA = new DamageSource(DamageSourceRef.LAVA);
	public static final DamageSource STUCK = new DamageSource(DamageSourceRef.STUCK);
	public static final DamageSource DROWN = new DamageSource(DamageSourceRef.DROWN);
	public static final DamageSource STARVE = new DamageSource(DamageSourceRef.STARVE);
	public static final DamageSource CACTUS = new DamageSource(DamageSourceRef.CACTUS);
	public static final DamageSource FALL = new DamageSource(DamageSourceRef.FALL);
	public static final DamageSource OUT_OF_WORLD = new DamageSource(DamageSourceRef.OUT_OF_WORLD);
	public static final DamageSource GENERIC = new DamageSource(DamageSourceRef.GENERIC);
	public static final DamageSource MAGIC = new DamageSource(DamageSourceRef.MAGIC);
	public static final DamageSource WITHER = new DamageSource(DamageSourceRef.WITHER);
	public static final DamageSource ANVIL = new DamageSource(DamageSourceRef.ANVIL);
	public static final DamageSource FALLING_BLOCK = new DamageSource(DamageSourceRef.FALLING_BLOCK);
	private static final DamageSource[] values = CommonUtil.getClassConstants(DamageSource.class);

	protected DamageSource(Object damageSource) {
		setHandle(damageSource);
	}

	public boolean isFireDamage() {
		return DamageSourceRef.isFireDamage(handle);
	}

	public boolean isExplosive() {
		return DamageSourceRef.isExplosive(handle);
	}

	/**
	 * Gets the Entity that dealt the damage
	 * 
	 * @return the Damager Entity, or null if there is none
	 */
	public Entity getEntity() {
		return DamageSourceRef.getEntity(handle);
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
