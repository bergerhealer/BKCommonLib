package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.Material;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartTNTHandle;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a TNT Block (explosive)
 */
public class CommonMinecartTNT extends CommonMinecart<ExplosiveMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            Material.getMaterial("TNT_MINECART") : Material.getMaterial("EXPLOSIVE_MINECART");

    /**
     * The velocity (squared) at which the Minecart is considered 'fast moving'.
     */
    public static final double FAST_MOVEMENT_SQUARED = 0.01;

    public CommonMinecartTNT(ExplosiveMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.TNT, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }

    /**
     * Explodes the TNT in this TNT Minecart. The velocity of the Minecart is
     * used to compute the yield modifier.
     */
    public void explode() {
        explode(vel.lengthSquared());
    }

    /**
     * Explodes the TNT in this TNT Minecart.
     *
     * @param yieldModifier - factor stating what yield the explosion has
     */
    public void explode(double yieldModifier) {
        EntityMinecartTNTHandle.T.explode.invoke(getHandle(), yieldModifier);
    }

    /**
     * Sets the fuse to the initial tick time and plays the detonation
     * animation/sound.
     */
    public void primeTNT() {
        EntityMinecartTNTHandle.T.prime.invoke(getHandle());
    }

    /**
     * Gets whether this TNT Minecart has primed
     *
     * @return True if the TNT is primed, False if not
     */
    public boolean isTNTPrimed() {
        return getFuseTicks() >= 0;
    }

    /**
     * Gets whether this TNT Minecart is moving fast. When moving at this speed,
     * colliding with things will result in detonation.
     *
     * @return True if moving fast, False if not
     */
    public boolean isMovingFast() {
        return vel.lengthSquared() > FAST_MOVEMENT_SQUARED;
    }

    /**
     * Gets the amount of ticks until this TNT Minecart will explode. A value of
     * -1 indicates that the TNT is not yet detonated.
     *
     * @return fuse ticks
     */
    public int getFuseTicks() {
        return EntityMinecartTNTHandle.T.fuse.getInteger(getHandle());
    }

    /**
     * Sets the amount of ticks until this TNT Minecart will explode
     *
     * @param fuseTicks to set to
     */
    public void setFuseTicks(int fuseTicks) {
        EntityMinecartTNTHandle.T.fuse.setInteger(getHandle(), fuseTicks);
    }
}
