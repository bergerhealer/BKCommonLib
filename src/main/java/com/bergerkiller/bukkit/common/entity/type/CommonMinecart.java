package com.bergerkiller.bukkit.common.entity.type;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityMinecart;

/**
 * An abstract implementation for all Minecarts
 *
 * @param <T> - type of Minecart Entity
 */
public class CommonMinecart<T extends Minecart> extends CommonEntity<T> {

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
        this.setWatchedData(NMSEntityMinecart.DATA_SHAKING_DIRECTION, direction);
    }

    public int getShakingDirection() {
        return this.getWatchedData(NMSEntityMinecart.DATA_SHAKING_DIRECTION, 0);
    }

    public void setShakingFactor(int factor) {
        this.setWatchedData(NMSEntityMinecart.DATA_SHAKING_FACTOR, factor);
    }

    public int getShakingFactor() {
        return this.getWatchedData(NMSEntityMinecart.DATA_SHAKING_FACTOR, 0);
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
        this.setWatchedData(NMSEntityMinecart.DATA_BLOCK_OFFSET, offsetPixels);
    }

    /**
     * Gets the vertical offset of the block in the Minecart, in pixels
     *
     * @return block offset in the Y-direction
     */
    public int getBlockOffset() {
        return this.getWatchedData(NMSEntityMinecart.DATA_BLOCK_OFFSET, 0);
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
        int value = this.getWatchedData(NMSEntityMinecart.DATA_BLOCK_TYPE, 0) & 0xFFFF;
        return MaterialUtil.getType(value);
    }

    /**
     * Gets the block data for this Minecart
     *
     * @return block data
     */
    public int getBlockData() {
        return this.getWatchedData(NMSEntityMinecart.DATA_BLOCK_TYPE, 0) >> 16;
    }

    /**
     * Sets the Block displayed in this Minecart
     *
     * @param blockType of the Block
     * @param blockData of the Block
     */
    public void setBlock(Material blockType, int blockData) {
        // Compile the new Block ID and Block Data into a single Integer entry (combining two short values)
        int entryId = MathUtil.clamp(blockType == null ? 0 : MaterialUtil.getTypeId(blockType), 0, Short.MAX_VALUE);
        int entryData = MathUtil.clamp(blockData, 0, Short.MAX_VALUE);
        int entryTotal = (entryId & 0xFFFF) | (entryData << 16);
        // Set the entry in the Entity data watcher, plus set INDEX=22 to 1 indicating there's a Block
        this.setWatchedData(NMSEntityMinecart.DATA_BLOCK_TYPE, entryTotal);
        this.setWatchedData(NMSEntityMinecart.DATA_BLOCK_VISIBLE, true);
    }

    @Override
    public boolean isVehicle() {
        return getHandle() instanceof net.minecraft.server.v1_11_R1.EntityMinecartRideable;
    }

    /**
     * Gets the position to align this Minecart on a slope
     *
     * @param x - coordinate of the old position
     * @param y - coordinate of the old position
     * @param z - coordinate of the old position
     * @return new Vector for the new sloped position, or null if none possible
     * (not a sloped rail)
     */
    public Vector getSlopedPosition(double x, double y, double z) {
    	//TODO: BROKEN!!!!
    	return null;
        //return Conversion.toVector.convert(getHandle(EntityMinecartAbstract.class).k(x, y, z));
    }
}
