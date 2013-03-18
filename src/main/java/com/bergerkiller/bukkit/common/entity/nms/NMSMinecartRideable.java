package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonMinecartRideable;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.EntityMinecartRideable;

public class NMSMinecartRideable extends EntityMinecartRideable implements NMSEntity {
	private CommonMinecartRideable entity;
	private EntityController<?> controller;

	public NMSMinecartRideable() {
		super(null);
	}

	@Override
	public CommonMinecartRideable getCommonEntity() {
		return entity;
	}

	@Override
	public EntityController<?> getController() {
		return this.controller;
	}

	@Override
	public void setController(EntityController<?> controller) {
		this.controller = controller;
		if (controller != null) {
			entity = (CommonMinecartRideable) controller.getEntity();
		}
	}

	@Override
	public boolean super_damageEntity(DamageSource damagesource, int damage) {
		return super.damageEntity(damagesource, damage);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, int damage) {
    	if (controller == null) {
    		return super_damageEntity(damagesource, damage);
    	} else {
    		return controller.onEntityDamage(CommonNMS.getEntity(damagesource.getEntity()), damage);
    	}
	}

	@Override
	public void super_onTick() {
		super.l_();
	}

	@Override
    public void l_() {
    	if (controller == null) {
    		super_onTick();
    	} else {
    		controller.onTick();
    	}
    }

	@Override
	public void move(double dx, double dy, double dz) {
		if (controller == null) {
			super.move(dx, dy, dz);
		} else {
			controller.onMove(dx, dy, dz);
		}
	}

	@Override
	public void super_onBurn(int damage) {
		super.burn(damage);
	}

	@Override
	public void burn(int damage) {
		if (controller == null) {
			super_onBurn(damage);
		} else {
			controller.onBurnDamage(damage);
		}
	}

	@Override
	public boolean a_(EntityHuman human) {
		if (controller == null) {
			return super_onInteract(human);
		} else {
			return controller.onInteractBy(CommonNMS.getHuman(human));
		}
	}

	@Override
	public boolean super_onInteract(EntityHuman interacter) {
		return super.a_(interacter);
	}

	@Override
	public void super_die() {
		super.die();
	}

	@Override
	public void die() {
		if (controller == null) {
			super_die();
		} else {
			controller.onDie();
		}
	}

	@Override
	public String super_getLocalizedName() {
		return super.getLocalizedName();
	}

	@Override
	public String getLocalizedName() {
		if (controller == null) {
			return super_getLocalizedName();
		} else {
			return controller.getLocalizedName();
		}
	}
}
