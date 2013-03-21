package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.EntityMinecartTNT;
import net.minecraft.server.v1_5_R1.NBTTagCompound;
import net.minecraft.server.v1_5_R1.World;

@SuppressWarnings("rawtypes")
public class NMSMinecartTNT extends EntityMinecartTNT implements NMSEntityHook {
	private EntityController<?> controller = new DefaultEntityController(this);

	public NMSMinecartTNT(World world) {
		super(world);
	}

	@Override
    public boolean c(NBTTagCompound nbttagcompound) {
        if (this.dead) {
        	return false;
        } else {
            nbttagcompound.setString("id", "MinecartChest");
            this.e(nbttagcompound);
            return true;
        }
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
	public boolean super_damageEntity(DamageSource damagesource, int damage) {
		return super.damageEntity(damagesource, damage);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, int damage) {
		return controller.onEntityDamage(CommonNMS.getEntity(damagesource.getEntity()), damage);
	}

	@Override
	public void super_onTick() {
		super.l_();
	}

	@Override
	public void l_() {
		controller.onTick();
	}

	@Override
	public void super_move(double dx, double dy, double dz) {
		super.move(dx, dy, dz);
	}

	@Override
	public void move(double dx, double dy, double dz) {
		controller.onMove(dx, dy, dz);
	}

	@Override
	public void super_onBurn(int damage) {
		super.burn(damage);
	}

	@Override
	public void burn(int damage) {
		controller.onBurnDamage(damage);
	}

	@Override
	public boolean a_(EntityHuman human) {
		return controller.onInteractBy(CommonNMS.getHuman(human));
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
		controller.onDie();
	}

	@Override
	public String super_getLocalizedName() {
		return super.getLocalizedName();
	}

	@Override
	public String getLocalizedName() {
		return controller.getLocalizedName();
	}
}
