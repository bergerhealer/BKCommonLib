package com.bergerkiller.bukkit.common.entity.type;

import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartAbstractHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartRideableHandle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

/**
 * An abstract implementation for all Minecarts
 *
 * @param <T> - type of Minecart Entity
 */
public class CommonMinecart<T extends Minecart> extends CommonEntity<T> {

    public final DataWatcher.EntityItem<Integer> metaShakingDirection = getDataItem(EntityMinecartAbstractHandle.DATA_SHAKING_DIRECTION);
    public final DataWatcher.EntityItem<Float>   metaShakingDamage    = getDataItem(EntityMinecartAbstractHandle.DATA_SHAKING_DAMAGE);
    public final DataWatcher.EntityItem<Integer> metaShakingFactor    = getDataItem(EntityMinecartAbstractHandle.DATA_SHAKING_FACTOR);
    public final DataWatcher.EntityItem<Integer> metaBlockOffset      = getDataItem(EntityMinecartAbstractHandle.DATA_BLOCK_OFFSET);

    public CommonMinecart(T base) {
        super(base);
    }

    public double getDamage() {
        return entity.getDamage();
    }

    public Vector getDerailedVelocityMod() {
        return entity.getDerailedVelocityMod();
    }

    public Vector getFlyingVelocityMod() {
        return entity.getFlyingVelocityMod();
    }

    public double getMaxSpeed() {
        return entity.getMaxSpeed();
    }

    public boolean isSlowWhenEmpty() {
        return entity.isSlowWhenEmpty();
    }

    public void setSlowWhenEmpty(boolean arg0) {
        entity.setSlowWhenEmpty(arg0);
    }

    public void setDamage(double damage) {
        entity.setDamage(damage);
    }

    public void setDerailedVelocityMod(Vector arg0) {
        entity.setDerailedVelocityMod(arg0);
    }

    public void setFlyingVelocityMod(Vector arg0) {
        entity.setFlyingVelocityMod(arg0);
    }

    public void setMaxSpeed(double arg0) {
        entity.setMaxSpeed(arg0);
    }

    public void setShakingDirection(int direction) {
        metaShakingDirection.set(direction);
    }

    public int getShakingDirection() {
        return metaShakingDirection.get();
    }

    public void setShakingFactor(int factor) {
        metaShakingFactor.set(factor);
    }

    public int getShakingFactor() {
        return metaShakingFactor.get();
    }

    /**
     * Gets all the drops to spawn when this Minecart is broken. The default
     * implementation (break up into parts) is used.
     *
     * @return list of drops (immutable)
     */
    public List<ItemStack> getBrokenDrops() { return Collections.emptyList(); }

    /**
     * Gets the combined Material type for this Minecart
     *
     * @return combined item Material type
     */
    public Material getCombinedItem() { return Material.MINECART; }

    /**
     * Sets the vertical offset of the block in the Minecart, in pixels
     *
     * @param offsetPixels to set to
     */
    public void setBlockOffset(int offsetPixels) {
        metaBlockOffset.set(offsetPixels);
    }

    /**
     * Gets the vertical offset of the block in the Minecart, in pixels
     *
     * @return block offset in the Y-direction
     */
    public int getBlockOffset() {
        return metaBlockOffset.get();
    }

    /**
     * Gets the block type for this Minecart<br>
     * <br>
     * <b>Deprecated: </b>use {@link #getBlock()} instead
     *
     * @return block type
     */
    @Deprecated
    public Material getBlockType() {
        return getBlock().getType();
    }

    /**
     * Sets the Block displayed in this Minecart<br>
     * <br>
     * <b>Deprecated: </b>use {@link #setBlock(BlockData)} instead
     *
     * @param blockType of the Block
     * @param blockData of the Block
     */
    @Deprecated
    public void setBlock(Material blockType, int blockData) {
        setBlock(BlockData.fromMaterialData(blockType, blockData));
    }

    /**
     * Gets the Block displayed in this Minecart
     * 
     * @return block
     */
    @SuppressWarnings("deprecation")
    public BlockData getBlock() {
        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            return getDataWatcher().get(EntityMinecartAbstractHandle.DATA_CUSTOM_DISPLAY_BLOCK);
        } else {
            Integer value = getDataWatcher().get(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE);
            return value == null ? BlockData.AIR : BlockData.fromCombinedId(value);
        }
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockType of the Block
     */
    public void setBlock(Material blockType) {
        setBlock(BlockData.fromMaterial(blockType));
    }

    /**
     * Sets the Block displayed in this Minecart
     * 
     * @param block
     */
    @SuppressWarnings("deprecation")
    public void setBlock(BlockData block) {
        DataWatcher meta = getDataWatcher();
        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            if (block.getType() == Material.AIR) {
                meta.set(EntityMinecartAbstractHandle.DATA_CUSTOM_DISPLAY_BLOCK, null);
            } else {
                meta.set(EntityMinecartAbstractHandle.DATA_CUSTOM_DISPLAY_BLOCK, block);
            }
        } else {
            if (block.getType() == Material.AIR) {
                meta.set(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE, 0);
                meta.set(EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE, false);
            } else {
                meta.set(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE, block.getCombinedId());
                meta.set(EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE, true);
            }
        }
    }

    @Override
    public boolean isVehicle() {
        return this.handle.isInstanceOf(EntityMinecartRideableHandle.T);
    }

    /**
     * Activates the minecart as if driving on top of an Activator Rail
     * 
     * @param activatorBlock of the activator rail
     * @param activated state of the activator rail
     */
    public void activate(Block activatorBlock, boolean activated) {
        EntityMinecartAbstractHandle.T.activateMinecart.invoke(getHandle(),
                activatorBlock.getWorld(), activatorBlock.getX(), activatorBlock.getY(), activatorBlock.getZ(), activated);
    }
}
