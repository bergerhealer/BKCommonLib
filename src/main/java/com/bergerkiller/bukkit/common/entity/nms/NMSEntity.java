package com.bergerkiller.bukkit.common.entity.nms;

import net.minecraft.server.v1_5_R1.DamageSource;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * Identifier so BKCommonLib knows that this Entity has been replaced.
 * All classes implementing this Interface should have an Empty Constructor.
 */
public interface NMSEntity {
	/**
	 * Gets the common entity assigned to this Entity
	 * 
	 * @return Common Entity
	 */
	public CommonEntity<?> getCommonEntity();

	/**
	 * Gets the Entity Controller of this Entity
	 * 
	 * @return entity controller
	 */
	public EntityController<?> getController();

	/**
	 * Sets the Entity Controller for this Entity
	 * 
	 * @param controller to set to
	 */
	public void setController(EntityController<?> controller);

	public void super_onTick();

	public boolean super_damageEntity(DamageSource damagesource, int damage);
}
