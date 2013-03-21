package com.bergerkiller.bukkit.common.entity.nms;

import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.EntityHuman;

import com.bergerkiller.bukkit.common.controller.EntityController;

/**
 * Identifier so BKCommonLib knows that this Entity has been replaced.
 * All classes implementing this Interface should have the default 'world only' constructor.
 */
public interface NMSEntityHook {
	public EntityController<?> getController();

	public void setController(EntityController<?> controller);

	public void super_onTick();

	public boolean super_onInteract(EntityHuman interacter);
	
	public boolean super_damageEntity(DamageSource damagesource, int damage);

	public void super_onBurn(int damage);

	public void super_move(double dx, double dy, double dz);

	public void super_die();

	public String super_getLocalizedName();
}
