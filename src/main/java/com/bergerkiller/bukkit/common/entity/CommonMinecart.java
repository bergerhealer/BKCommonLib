package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

public abstract class CommonMinecart<T extends Minecart> extends CommonEntity<T> implements Minecart {

	public CommonMinecart(T base) {
		super(base);
	}

	@Override
	public int getDamage() {
		return base.getDamage();
	}

	@Override
	public Vector getDerailedVelocityMod() {
		return base.getDerailedVelocityMod();
	}

	@Override
	public Vector getFlyingVelocityMod() {
		return base.getFlyingVelocityMod();
	}

	@Override
	public double getMaxSpeed() {
		return base.getMaxSpeed();
	}

	@Override
	public boolean isSlowWhenEmpty() {
		return base.isSlowWhenEmpty();
	}

	@Override
	public void setSlowWhenEmpty(boolean arg0) {
		base.setSlowWhenEmpty(arg0);
	}

	@Override
	public void setDamage(int arg0) {
		base.setDamage(arg0);
	}

	@Override
	public void setDerailedVelocityMod(Vector arg0) {
		base.setDerailedVelocityMod(arg0);
	}

	@Override
	public void setFlyingVelocityMod(Vector arg0) {
		base.setFlyingVelocityMod(arg0);
	}

	@Override
	public void setMaxSpeed(double arg0) {
		base.setMaxSpeed(arg0);
	}
}
