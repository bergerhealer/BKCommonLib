package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartFurnaceHandle;
import org.bukkit.Material;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Furnace
 */
public class CommonMinecartFurnace extends CommonMinecart<PoweredMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            Material.getMaterial("FURNACE_MINECART") : Material.getMaterial("POWERED_MINECART");

    /**
     * The amount of fuel ticks a single item of coal gives to a furnace
     * minecart
     */
    public static final int COAL_FUEL = 3600;

    public final DataWatcher.EntityItem<Boolean> metaSmoking = getDataItem(EntityMinecartFurnaceHandle.DATA_SMOKING);

    public CommonMinecartFurnace(PoweredMinecart base) {
        super(base);
    }

    public int getFuelTicks() {
        return EntityMinecartFurnaceHandle.T.fuel.getInteger(getHandle());
    }

    public void setFuelTicks(int fuelTicks) {
        EntityMinecartFurnaceHandle.T.fuel.setInteger(getHandle(), fuelTicks);
    }

    public boolean hasFuel() {
        return getFuelTicks() > 0;
    }

    public void addFuelTicks(int amount) {
        setFuelTicks(getFuelTicks() + amount);
    }

    public Vector getPush() {
        return EntityMinecartFurnaceHandle.T.getPushForce.invoke(getHandle());
    }

    public void setPush(double fx, double fy, double fz) {
        EntityMinecartFurnaceHandle.createHandle(getHandle()).setPushForce(fx, fy, fz);
    }

    public void setPush(Vector push) {
        setPush(push.getX(), push.getY(), push.getZ());
    }

    /**
     * @deprecated Use {@link #getPush()}
     */
    @Deprecated
    public double getPushX() {
        return EntityMinecartFurnaceHandle.createHandle(getHandle()).getPushForceX();
    }

    /**
     * @deprecated Use {@link #setPush(double, double, double)} or {@link #setPush(Vector)}
     */
    @Deprecated
    public void setPushX(double pushX) {
        EntityMinecartFurnaceHandle.createHandle(getHandle()).setPushForceX(pushX);
    }

    /**
     * @deprecated Use {@link #getPush()}
     */
    @Deprecated
    public double getPushZ() {
        return EntityMinecartFurnaceHandle.createHandle(getHandle()).getPushForceZ();
    }

    /**
     * @deprecated Use {@link #setPush(double, double, double)} or {@link #setPush(Vector)}
     */
    @Deprecated
    public void setPushZ(double pushZ) {
        EntityMinecartFurnaceHandle.createHandle(getHandle()).setPushForceX(pushZ);
    }

    public boolean isSmoking() {
        return metaSmoking.get();
    }

    public void setSmoking(boolean smoking) {
        metaSmoking.set(smoking);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.FURNACE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }
}
