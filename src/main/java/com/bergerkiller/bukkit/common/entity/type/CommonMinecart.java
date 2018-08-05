package com.bergerkiller.bukkit.common.entity.type;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartAbstractHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartRideableHandle;

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
    public final DataWatcher.EntityItem<Integer> metaBlockType        = getDataItem(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE);
    public final DataWatcher.EntityItem<Boolean> metaBlockVisible     = getDataItem(EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE);

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
     * Gets an identifier for this type of Minecart
     *
     * @return Minecart type ID
     */
    public int getMinecartType() {
        return EntityMinecartAbstractHandle.T.getType.invoke(getHandle());
    }

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
     * Gets the block data for this Minecart<br>
     * <br>
     * <b>Deprecated: </b>use {@link #getBlock()} instead
     *
     * @return block data
     */
    @Deprecated
    public int getBlockData() {
        return metaBlockType.get() >> 16;
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
        return BlockData.fromCombinedId(metaBlockType.get());
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
        if (block.getType() == Material.AIR) {
            metaBlockType.set(0);
            metaBlockVisible.set(false);
            return;
        } else {
            metaBlockType.set(block.getCombinedId());
            metaBlockVisible.set(true);
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
        EntityMinecartAbstractHandle.T.activate.invoke(getHandle(),
                activatorBlock.getX(), activatorBlock.getY(), activatorBlock.getZ(), activated);
    }
}
