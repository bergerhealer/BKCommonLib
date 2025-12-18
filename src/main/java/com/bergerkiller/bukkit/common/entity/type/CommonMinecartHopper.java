package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartHopperHandle;
import org.bukkit.Material;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Hopper
 */
public class CommonMinecartHopper extends CommonMinecartInventory<HopperMinecart> {

    public CommonMinecartHopper(HopperMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.HOPPER, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.HOPPER_MINECART;
    }

    /**
     * Sucks all nearby Items into this Hopper Minecart
     *
     * @return True if Items were sucked in, False if not
     */
    public boolean suckItems() {
        return EntityMinecartHopperHandle.T.suckItems.invoke(this.getHandle());
    }

    /**
     * Gets whether this Hopper Minecart is currently sucking in nearby Items
     *
     * @return True if sucking items, False if not
     */
    public boolean isSuckingItems() {
        return EntityMinecartHopperHandle.T.isSuckingEnabled.invoke(this.getHandle());
    }

    /**
     * Sets whether this Hopper Minecart is currently sucking in nearby Items
     *
     * @param sucking state to set to
     */
    public void setSuckingItems(boolean sucking) {
        EntityMinecartHopperHandle.T.setSuckingEnabled.invoke(this.getHandle(), sucking);
    }

    /**
     * Sets the remaining ticks until the next Item sucking operation occurs
     *
     * @param cooldownTicks to set to
     * @deprecated This method doesn't do anything and never did
     */
    @Deprecated
    public void setSuckingCooldown(int cooldownTicks) {
    }

    /**
     * Gets the remaining ticks until the next Item sucking operation occurs
     *
     * @return sucking cooldown ticks
     * @deprecated This method always returns 0. It never was used for anything.
     */
    @Deprecated
    public int getSuckingCooldown() {
        return 0;
    }
}
