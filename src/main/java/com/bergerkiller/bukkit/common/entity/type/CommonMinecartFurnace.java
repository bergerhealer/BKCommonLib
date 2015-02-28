package com.bergerkiller.bukkit.common.entity.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

/**
 * A Common Entity implementation for Minecarts with a Furnace
 */
public class CommonMinecartFurnace extends CommonMinecart<PoweredMinecart> {

    private static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityMinecartFurnace");
    private static final FieldAccessor<Double> pushForceX = TEMPLATE.getField("a");
    private static final FieldAccessor<Double> pushForceZ = TEMPLATE.getField("b");
    private static final FieldAccessor<Integer> fuel = TEMPLATE.getField("c");
    private static final MethodAccessor<Boolean> isSmokingMethod = TEMPLATE.getMethod("j");//e
    private static final MethodAccessor<Void> setSmokingMethod = TEMPLATE.getMethod("i", boolean.class);//f

    /**
     * The amount of fuel ticks a single item of coal gives to a furnace
     * minecart
     */
    public static final int COAL_FUEL = 3600;

    public CommonMinecartFurnace(PoweredMinecart base) {
        super(base);
    }

    public int getFuelTicks() {
        return fuel.get(getHandle());
    }

    public void setFuelTicks(int fuelTicks) {
        fuel.set(getHandle(), fuelTicks);
    }

    public boolean hasFuel() {
        return getFuelTicks() > 0;
    }

    public void addFuelTicks(int amount) {
        setFuelTicks(getFuelTicks() + amount);
    }

    public double getPushX() {
        return pushForceX.get(getHandle());
    }

    public void setPushX(double pushX) {
        pushForceX.set(getHandle(), pushX);
    }

    public double getPushZ() {
        return pushForceZ.get(getHandle());
    }

    public void setPushZ(double pushZ) {
        pushForceZ.set(getHandle(), pushZ);
    }

    public boolean isSmoking() {
        return isSmokingMethod.invoke(getHandle());
    }

    public void setSmoking(boolean smoking) {
        setSmokingMethod.invoke(getHandle(), smoking);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.FURNACE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.POWERED_MINECART;
    }
}
