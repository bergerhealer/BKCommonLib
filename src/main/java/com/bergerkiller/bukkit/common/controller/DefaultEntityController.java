package com.bergerkiller.bukkit.common.controller;

import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.EntityLiving;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;

/**
 * Does nothing but redirect to the default entity behaviour
 * 
 * @param <T> - type of Common Entity
 */
public final class DefaultEntityController<T extends CommonEntity<?>> extends EntityController<T> {

	@Override
	public void onDie() {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			((NMSEntityHook) handle).super_die();
		} else {
			((Entity) handle).die();
		}
	}

	@Override
	public void onTick() {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			((NMSEntityHook) handle).super_onTick();
		} else {
			((Entity) handle).l_();
		}
	}

	@Override
	public boolean onInteractBy(HumanEntity interacter) {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			return ((NMSEntityHook) handle).super_onInteract(CommonNMS.getNative(interacter));
		} else {
			return ((Entity) handle).a_(CommonNMS.getNative(interacter));
		}
	}

	@Override
	public boolean onEntityDamage(org.bukkit.entity.Entity damager, int damage) {
		DamageSource source;
		if (damager instanceof Player) {
			source = DamageSource.playerAttack(CommonNMS.getNative(damager, EntityHuman.class));
		} else if (damager instanceof LivingEntity) {
			source = DamageSource.mobAttack(CommonNMS.getNative(damager, EntityLiving.class));
		} else {
			source = DamageSource.GENERIC;
		}
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			return ((NMSEntityHook) handle).super_damageEntity(source, damage);
		} else {
			return ((Entity) handle).damageEntity(source, damage);
		}
	}

	@Override
	public void onBurnDamage(int damage) {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			((NMSEntityHook) handle).super_onBurn(damage);
		} else {
			EntityRef.burn(handle, damage);
		}
	}

	@Override
	public String getLocalizedName() {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			return ((NMSEntityHook) handle).super_getLocalizedName();
		} else {
			return ((Entity) handle).getLocalizedName();
		}
	}

	@Override
	public void onMove(double dx, double dy, double dz) {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityHook) {
			((NMSEntityHook) handle).super_move(dx, dy, dz);
		} else {
			((Entity) handle).move(dx, dy, dz);
		}
	}
}
