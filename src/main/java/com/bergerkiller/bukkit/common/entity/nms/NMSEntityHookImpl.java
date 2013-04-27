package com.bergerkiller.bukkit.common.entity.nms;

import net.minecraft.server.v1_5_R2.DamageSource;
import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityHuman;
import net.minecraft.server.v1_5_R2.NBTTagCompound;

import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTypesRef;

public class NMSEntityHookImpl implements NMSEntityHook {
	private EntityController<?> controller;

	public NMSEntityHookImpl(CommonEntity<?> entity) {
		this.controller = new DefaultEntityController();
		this.controller.bind(entity);
	}

	@Override
	public EntityController<?> getController() {
		return this.controller;
	}

	@Override
	public void setController(EntityController<?> controller) {
		this.controller = controller;
	}

	@Override
	public boolean a_(EntityHuman human) {
		return controller.onInteractBy(CommonNMS.getHuman(human));
	}

	@Override
	public void l_() {
		controller.onTick();
	}

	@Override
	public boolean damageEntity(DamageSource damageSource, int damage) {
		controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
		return true;
	}

	@Override
	public void burn(int damage) {
		controller.onBurnDamage(damage);
	}

	@Override
	public void move(double dx, double dy, double dz) {
		controller.onMove(dx, dy, dz);
	}

	@Override
	public void die() {
		controller.onDie();
	}

	@Override
	public String getLocalizedName() {
		return controller.getLocalizedName();
	}

	private String getSavedName() {
		return EntityTypesRef.classToNames.get(controller.getEntity().getHandle().getClass().getSuperclass());
	}

	@Override
	public boolean c(NBTTagCompound nbttagcompound) {
		if (this.controller.getEntity().isDead()) {
			return false;
		} else {
			nbttagcompound.setString("id", getSavedName());
			this.controller.getEntity().getHandle(Entity.class).e(nbttagcompound);
			return true;
		}
	}

	@Override
	public boolean d(NBTTagCompound nbttagcompound) {
		if (this.controller.getEntity().isDead() || (this.controller.getEntity().hasPlayerPassenger() && controller.isPlayerTakable())) {
			return false;
		} else {
			nbttagcompound.setString("id", getSavedName());
			this.controller.getEntity().getHandle(Entity.class).e(nbttagcompound);
			return true;
		}
	}

	/*
	 * The super methods are unused
	 */
	@Override
	public boolean super_a_(EntityHuman human) {
		return controller.getEntity().getHandle(NMSEntityHook.class).super_a_(human);
	}

	@Override
	public void super_l_() {
		controller.getEntity().getHandle(NMSEntityHook.class).super_l_();
	}

	@Override
	public boolean super_damageEntity(DamageSource damagesource, int damage) {
		return controller.getEntity().getHandle(NMSEntityHook.class).super_damageEntity(damagesource, damage);
	}

	@Override
	public void super_burn(int damage) {
		controller.getEntity().getHandle(NMSEntityHook.class).super_burn(damage);
	}

	@Override
	public void super_move(double dx, double dy, double dz) {
		controller.getEntity().getHandle(NMSEntityHook.class).super_move(dx, dy, dz);
	}

	@Override
	public void super_die() {
		controller.getEntity().getHandle(NMSEntityHook.class).super_die();
	}

	@Override
	public String super_getLocalizedName() {
		return controller.getEntity().getHandle(NMSEntityHook.class).super_getLocalizedName();
	}
}
