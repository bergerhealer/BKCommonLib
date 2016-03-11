package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.EntityController;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EnumHand;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.Location;

/**
 * The default methods provided by all Entity types
 */
public interface NMSEntityHook {

	public EntityController<?> getController();

	public void setController(EntityController<?> controller);

	/**
	 * onInteractBy super
	 */
	//Changed
	public boolean super_a(EntityHuman human, ItemStack itemstack, EnumHand enumHand);

	/**
	 * onInteractBy
	 */
	public boolean a(EntityHuman human, ItemStack itemstack, EnumHand enumHand);

	/**
	 * onTick super
	 */
	public void super_m();

	/**
	 * onTick
	 */
	public void m();

	/**
	 * Damage Entity super
	 */
	public boolean super_damageEntity(DamageSource damagesource, float damage);

	/**
	 * Damage Entity
	 */
	public boolean damageEntity(DamageSource damagesource, float damage);

	/**
	 * onBurn super
	 */
	public void super_burn(float damage);

	/**
	 * onBurn
	 */
	public void burn(float damage);

	/**
	 * onPush super
	 */
	public void super_g(double dx, double dy, double dz);

	/**
	 * onPush
	 */
	public void g(double dx, double dy, double dz);

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
	public String super_getName();

	/**
	 * getLocalizedName
	 */
	public String getName();

	/**
	 * onSave
	 */
	public boolean c(NBTTagCompound nbttagcompound);

	/**
	 * onSave for takeables
	 */
	public boolean d(NBTTagCompound nbttagcompound);

	/**
	 * Teleport super
	 */
	public void super_teleportTo(Location exit, boolean portal);

	/**
	 * Teleport
	 */
	public void teleportTo(Location exit, boolean portal);
}
