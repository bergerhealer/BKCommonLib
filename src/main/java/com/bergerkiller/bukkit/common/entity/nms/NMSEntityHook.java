package com.bergerkiller.bukkit.common.entity.nms;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;

import com.bergerkiller.bukkit.common.controller.EntityController;

/**
 * The default methods provided by all Entity types
 */
public interface NMSEntityHook {
	public EntityController<?> getController();

	public void setController(EntityController<?> controller);

	/**
	 * onInteractBy super
	 */
	public boolean super_a_(EntityHuman human);

	/**
	 * onInteractBy
	 */
	public boolean a_(EntityHuman human);

	/**
	 * onTick super
	 */
	public void super_l_();

	/**
	 * onTick
	 */
	public void l_();

	/**
	 * Damage Entity super
	 */
	public boolean super_damageEntity(DamageSource damagesource, int damage);

	/**
	 * Damage Entity
	 */
	public boolean damageEntity(DamageSource damagesource, int damage);

	/**
	 * onBurn super
	 */
	public void super_burn(int damage);

	/**
	 * onBurn
	 */
	public void burn(int damage);

	/**
	 * onMove super
	 */
	public void super_move(double dx, double dy, double dz);

	/**
	 * onMove
	 */
	public void move(double dx, double dy, double dz);

	/**
	 * onDie super
	 */
	public void super_die();

	/**
	 * onDie
	 */
	public void die();

	/**
	 * getLocalizedName super
	 */
	public String super_getLocalizedName();

	/**
	 * getLocalizedName
	 */
	public String getLocalizedName();

	/**
	 * onSave
	 */
    public boolean c(NBTTagCompound nbttagcompound);

	/**
	 * onSave for takeables
	 */
    public boolean d(NBTTagCompound nbttagcompound);
}
