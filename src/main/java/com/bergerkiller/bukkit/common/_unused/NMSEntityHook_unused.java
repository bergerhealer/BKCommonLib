package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.bukkit.common.controller.EntityController;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Location;

/**
 * The default methods provided by all Entity types
 */
public interface NMSEntityHook_unused {

	EntityController<?> getController();

	void setController(EntityController<?> controller);

	/**
	 * onInteractBy super
	 */
	//Changed
	boolean super_a(EntityHuman human, ItemStack itemstack, EnumHand enumHand);

	/**
	 * onInteractBy
	 */
	boolean a(EntityHuman human, ItemStack itemstack, EnumHand enumHand);

	/**
	 * onTick super
	 */
	void super_U();

	/**
	 * onTick
	 */
	void u();

	/**
	 * Damage Entity super
	 */
	boolean super_damageEntity(DamageSource damagesource, float damage);

	/**
	 * Damage Entity
	 */
	boolean damageEntity(DamageSource damagesource, float damage);

	/**
	 * onBurn super
	 */
	void super_burn(float damage);

	/**
	 * onBurn
	 */
	void burn(float damage);

	/**
	 * onPush super
	 */
	void super_g(double dx, double dy, double dz);

	/**
	 * onPush
	 */
	void g(double dx, double dy, double dz);

	/**
	 * onMove super
	 */
	void super_move(double dx, double dy, double dz);

	/**
	 * onMove
	 */
	void move(double dx, double dy, double dz);

	/**
	 * onDie super
	 */
	void super_die();

	/**
	 * onDie
	 */
	void die();

	/**
	 * getLocalizedName super
	 */
	String super_getName();

	/**
	 * getLocalizedName
	 */
	String getName();

	/**
	 * onSave
	 */
	boolean c(NBTTagCompound nbttagcompound);

	/**
	 * onSave for takeables
	 */
	boolean d(NBTTagCompound nbttagcompound);

	/**
	 * Teleport super
	 */
	void super_teleportTo(Location exit, boolean portal);

	/**
	 * Teleport
	 */
	void teleportTo(Location exit, boolean portal);
}
