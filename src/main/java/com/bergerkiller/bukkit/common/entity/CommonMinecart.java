package com.bergerkiller.bukkit.common.entity;

import java.util.List;

import net.minecraft.server.v1_5_R1.EntityMinecartAbstract;

import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class CommonMinecart<T extends Minecart> extends CommonEntity<T> {

	public CommonMinecart(T base) {
		super(base);
	}

	public int getDamage() {
		return entity.getDamage();
	}

	public Vector getDerailedVelocityMod() {
		return entity.getDerailedVelocityMod();
	}

	public Vector getFlyingVelocityMod() {
		return entity.getFlyingVelocityMod();
	}

	public double getMaxSpeed() {
		return entity.getMaxSpeed();
	}

	public boolean isSlowWhenEmpty() {
		return entity.isSlowWhenEmpty();
	}

	public void setSlowWhenEmpty(boolean arg0) {
		entity.setSlowWhenEmpty(arg0);
	}

	public void setDamage(int arg0) {
		entity.setDamage(arg0);
	}

	public void setDerailedVelocityMod(Vector arg0) {
		entity.setDerailedVelocityMod(arg0);
	}

	public void setFlyingVelocityMod(Vector arg0) {
		entity.setFlyingVelocityMod(arg0);
	}

	public void setMaxSpeed(double arg0) {
		entity.setMaxSpeed(arg0);
	}

	public void setShakingDirection(int direction) {
		getHandle(EntityMinecartAbstract.class).j(direction); 
	}

	public int getShakingDirection() {
		return getHandle(EntityMinecartAbstract.class).k();
	}

	public void setShakingFactor(int factor) {
		getHandle(EntityMinecartAbstract.class).i(factor); 
	}

	public int getShakingFactor() {
		return getHandle(EntityMinecartAbstract.class).j();
	}

	/**
	 * Gets all the drops to spawn when this Minecart is broken.
	 * The default implementation (break up into parts) is used.
	 * 
	 * @return list of drops (immutable)
	 */
	public abstract List<ItemStack> getBrokenDrops();

	/**
	 * Gets the combined Material type for this Minecart
	 * 
	 * @return combined item Material type
	 */
	public abstract Material getCombinedItem();
}
