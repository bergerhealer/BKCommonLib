package com.bergerkiller.bukkit.common.entity.type;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityMinecart;

/**
 * An abstract implementation for all Minecarts
 *
 * @param <T> - type of Minecart Entity
 */
public class CommonMinecart<T extends Minecart> extends CommonEntity<T> {

    public final DataWatcher.EntityItem<Integer> metaShakingDirection = getDataItem(NMSEntityMinecart.DATA_SHAKING_DIRECTION);
    public final DataWatcher.EntityItem<Float>   metaShakingDamage    = getDataItem(NMSEntityMinecart.DATA_SHAKING_DAMAGE);
    public final DataWatcher.EntityItem<Integer> metaShakingFactor    = getDataItem(NMSEntityMinecart.DATA_SHAKING_FACTOR);
    public final DataWatcher.EntityItem<Integer> metaBlockOffset      = getDataItem(NMSEntityMinecart.DATA_BLOCK_OFFSET);
    public final DataWatcher.EntityItem<Integer> metaBlockType        = getDataItem(NMSEntityMinecart.DATA_BLOCK_TYPE);
    public final DataWatcher.EntityItem<Boolean> metaBlockVisible     = getDataItem(NMSEntityMinecart.DATA_BLOCK_VISIBLE);

    public CommonMinecart(T base) {
        super(base);
    }

    public double getDamage() {
        return getHandle(net.minecraft.server.v1_11_R1.EntityMinecartAbstract.class).getDamage();
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
        return getHandle(net.minecraft.server.v1_11_R1.EntityMinecartAbstract.class).getType();
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
     * Gets the block type id for this Minecart
     *
     * @return block type id
     */
    @Deprecated
    public int getBlockId() {
        Material mat = getBlockType();
        return mat == null ? 0 : mat.getId();
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockId of the Block
     */
    @Deprecated
    public void setBlock(int blockId) {
        setBlock(blockId, 0);
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockId of the Block
     * @param blockData of the Block
     */
    @Deprecated
    public void setBlock(int blockId, int blockData) {
        setBlock(Material.getMaterial(blockId), blockData);
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockType of the Block
     */
    public void setBlock(Material blockType) {
        setBlock(blockType, 0);
    }

    /**
     * Gets the block type for this Minecart
     *
     * @return block type
     */
    public Material getBlockType() {
        int value = metaBlockType.get() & 0xFFFF;
        return MaterialUtil.getType(value);
    }

    /**
     * Gets the block data for this Minecart
     *
     * @return block data
     */
    public int getBlockData() {
        return metaBlockType.get() >> 16;
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockType of the Block
     * @param blockData of the Block
     */
    public void setBlock(Material blockType, int blockData) {
        if (blockType == Material.AIR) {
            metaBlockVisible.set(false);
            return;
        }
        // Compile the new Block ID and Block Data into a single Integer entry (combining two short values)
        int entryId = MathUtil.clamp(blockType == null ? 0 : MaterialUtil.getTypeId(blockType), 0, Short.MAX_VALUE);
        int entryData = MathUtil.clamp(blockData, 0, Short.MAX_VALUE);
        int entryTotal = (entryId & 0xFFFF) | (entryData << 16);
        // Set the entry in the Entity data watcher, plus set INDEX=22 to 1 indicating there's a Block
        metaBlockType.set(entryTotal);
        metaBlockVisible.set(true);
    }

    @Override
    public boolean isVehicle() {
        return getHandle() instanceof net.minecraft.server.v1_11_R1.EntityMinecartRideable;
    }

    /**
     * Activates the minecart as if driving on top of an Activator Rail
     * 
     * @param activatorBlock of the activator rail
     * @param activated state of the activator rail
     */
    public void activate(Block activatorBlock, boolean activated) {
        NMSEntityMinecart.activate.invoke(getHandle(), 
                activatorBlock.getX(), activatorBlock.getY(), activatorBlock.getZ(), activated);
    }
}
