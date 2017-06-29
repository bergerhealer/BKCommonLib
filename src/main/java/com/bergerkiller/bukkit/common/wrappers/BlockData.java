package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;

/**
 * Stores a reference to net.minecraft.server.Block and IBlockData objects,
 * providing access to Block and Block Data properties.
 */
public abstract class BlockData extends BlockDataRegistry {

    /**
     * Applies data from a Block to this BlockData
     * 
     * @param block to apply
     */
    public abstract void loadBlock(Object block);

    /**
     * Applies data from IBlockData to this BlockData
     * 
     * @param iBlockData to apply
     */
    public abstract void loadBlockData(Object iBlockData);

    /**
     * Applies legacy Material and data to this BlockData
     * 
     * @param material to apply
     * @param data for the material to apply
     */
    @Deprecated
    public abstract void loadMaterialData(Material material, int data);

    /**
     * Obtains the RAW internal Block handle this BlockData represents.
     * Should not be used.
     * 
     * @return Block
     */
    public final Object getBlockRaw() {
        return getBlock().getRaw();
    }

    /**
     * Obtains the internal Block handle this BlockData represents
     * 
     * @return Block
     */
    public abstract BlockHandle getBlock();

    /**
     * Obtains the internal IBlockData handle this BlockData represents
     * 
     * @return IBlockData
     */
    public abstract Object getData();

    @Override
    public String toString() {
        Object b = getData();
        return (b == null) ? "[null]" : b.toString();
    }

    /**
     * Gets the ID of the Block, ranging 0 - 255
     * 
     * @return Raw Block Type Id
     */
    @Deprecated
    public abstract int getTypeId();

    /**
     * Gets the Raw Data valueof the Block, ranging 0 - 15
     * 
     * @return Raw Block Data
     */
    @Deprecated
    public abstract int getRawData();

    /**
     * Gets a combined Id, combining type id and data into a single value
     * 
     * @return combined id
     */
    @Deprecated
    public abstract int getCombinedId();

    /**
     * Gets a combined char Id, used on MC 1.8.8 in By Id data
     * 
     * @return combined ID. Only used internally.
     */
    @Deprecated
    public abstract int getCombinedId_1_8_8();

    /**
     * Gets the Material Type of the Block
     * 
     * @return Block Material Type
     */
    @SuppressWarnings("deprecation")
    public final org.bukkit.Material getType() {
        return org.bukkit.Material.getMaterial(getTypeId());
    }

    /**
     * Creates a new MaterialData instance appropriate for this Block
     * 
     * @return Block Material Data
     */
    @SuppressWarnings("deprecation")
    public final MaterialData newMaterialData() {
        Material type = this.getType();

        // Null: return AIR
        if (type == null) {
            return new MaterialData(0, (byte) 0);
        }

        // Create new MaterialData + some fixes.
        final MaterialData mdata;
        if (type == Material.GOLD_PLATE || type == Material.IRON_PLATE) {
            // Bukkit bugfix.
            mdata = new org.bukkit.material.PressurePlate(type, (byte) this.getRawData());
        } else {
            mdata = type.getNewData((byte) this.getRawData());
        }

        // Fix attachable face returning NULL sometimes
        if (mdata instanceof Attachable) {
            Attachable att = (Attachable) mdata;
            if (att.getAttachedFace() == null) {
                att.setFacingDirection(BlockFace.NORTH);
            }
        }
        return mdata;
    }

    /**
     * Creates a new MaterialData instance appropriate for this Block, and
     * attempts to cast it to a particular Material type. If casting fails,
     * null is returned instead.
     * 
     * @param type of Material Data
     * @return Block Material Data, or null on failure
     */
    public final <T extends MaterialData> T newMaterialData(Class<T> type) {
        return CommonUtil.tryCast(newMaterialData(), type);
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    /**
     * Gets the name of the sound effect played when building or stepping on this Block
     * 
     * @return step sound
     */
    public abstract String getStepSound();
    
    /**
     * Gets the opacity of the Block, a value between 0 and 15.
     * A value of 255 indicates full opaque-ness. A value of 0 is fully transparent.
     *
     * @return the opacity
     */
    public abstract int getOpacity();

    /**
     * Gets the amount of light the Block radiates, value between 0 and 15.
     *
     * @return light emission
     */
    public abstract int getEmission();

    /**
     * Gets whether this Block is fully occluding, not allowing light to pass through
     * 
     * @return True if fully occluding, False if not
     */
    public abstract boolean isOccluding();

    /**
     * Gets whether this Block causes suffocation damage to players inside
     * 
     * @return True if suffocation occurs, False if not
     */
    public abstract boolean isSuffocating();

    /**
     * Gets whether this Block is a source of Redstone Power
     * 
     * @return True if the block is a power source, False if not
     */
    public abstract boolean isPowerSource();

    /**
     * Gets the damage resilience of a block to damage dealt by a certain entity
     *
     * @param source of the damage
     * @return resilience value
     */
    public abstract float getDamageResilience(Entity source);

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param block to drop at
     * @param yield
     */
    public final void dropNaturally(Block block, float yield) {
        dropNaturally(block, yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param block to drop at
     * @param yield
     * @param chance
     */
    public final void dropNaturally(Block block, float yield, int chance) {
        dropNaturally(block.getWorld(), block.getX(), block.getY(), block.getZ(), yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield
     */
    public final void dropNaturally(World world, int x, int y, int z, float yield) {
        dropNaturally(world, x, y, z, yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield
     * @param chance
     */
    public abstract void dropNaturally(World world, int x, int y, int z, float yield, int chance);

    /**
     * Ignites the block (for example, ignites TNT)
     *
     * @param block to ignite at
     */
    public final void ignite(Block block) {
        ignite(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * Ignites the block (for example, ignites TNT)
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     */
    public abstract void ignite(World world, int x, int y, int z);

    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param block to destroy
     * @param yield (e.g. 20.0f)
     */
    public final void destroy(Block block, float yield) {
        destroy(block.getWorld(), block.getX(), block.getY(), block.getZ(), yield);
    }

    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield (e.g. 20.0f)
     */
    public abstract void destroy(World world, int x, int y, int z, float yield);

    /**
     * Handles an Entity stepping on a Block
     * 
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param entity that stepped on the block
     */
    public final void stepOn(World world, int x, int y, int z, Entity entity) {
        stepOn(world, new IntVector3(x, y, z), entity);
    }

    /**
     * Handles an Entity stepping on a Block
     * 
     * @param world the block is in
     * @param blockPosition of the block
     * @param entity that stepped on the block
     */
    public abstract void stepOn(World world, IntVector3 blockPosition, Entity entity);

}
