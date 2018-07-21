package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartFurnaceHandle;

import org.bukkit.Material;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.inventory.ItemStack;

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

    public double getPushX() {
        return EntityMinecartFurnaceHandle.T.pushForceX.getDouble(getHandle());
    }

    public void setPushX(double pushX) {
        EntityMinecartFurnaceHandle.T.pushForceX.setDouble(getHandle(), pushX);
    }

    public double getPushZ() {
        return EntityMinecartFurnaceHandle.T.pushForceZ.getDouble(getHandle());
    }

    public void setPushZ(double pushZ) {
        EntityMinecartFurnaceHandle.T.pushForceZ.setDouble(getHandle(), pushZ);
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
