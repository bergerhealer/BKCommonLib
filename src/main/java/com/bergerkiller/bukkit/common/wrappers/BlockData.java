package com.bergerkiller.bukkit.common.wrappers;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

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
    public final void loadMaterialData(Material material, int data) {
        loadMaterialData(new MaterialData(material, (byte) data));
    }

    /**
     * Applies legacy Material and data to this BlockData
     * 
     * @param material to apply
     * @param data for the material to apply
     */
    @Deprecated
    public abstract void loadMaterialData(MaterialData materialdata);

    /**
     * Obtains the RAW internal Block handle this BlockData represents.
     * Should not be used.
     * 
     * @return Block
     */
    public final Object getBlockRaw() {
        return Handle.getRaw(getBlock());
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BlockData) {
            BlockData data = (BlockData) o;
            return data.getCombinedId() == this.getCombinedId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getCombinedId();
    }

    @Override
    public String toString() {
        Object b = getData();
        return (b == null) ? "[null]" : b.toString();
    }

    /**
     * Gets the name of this Block
     * 
     * @return block name
     */
    public abstract String getBlockName();

    /**
     * Serializes this BlockData to a String, preserving the block information and the
     * states set. This same BlockData can then be obtained using {@link BlockDataRegistry#fromString(String)}.
     * The serialized text is cross-version compatible.<br>
     * <br>
     * Example output:
     * <pre>minecraft:furnace[facing=east,lit=true]</pre>
     * 
     * @return serialized String representation of this BlockData
     */
    public abstract String serializeToString();

    /**
     * Gets a mapping of key:value pairs for all the default data-encoded render options set for this BlockData.
     * For example, stairs will add the <i>facing: west</i> key-value pair.
     * The returned options are as if the Block is free-standing in an empty void of Air.
     * 
     * @return default render options
     */
    public final BlockRenderOptions getDefaultRenderOptions() {
        return getRenderOptions(null, 0, 0, 0);
    }

    /**
     * Gets a mapping of key:value pairs for all the data-encoded render options set for this BlockData.
     * For example, stairs will add the <i>facing: west</i> key-value pair.
     * The returned object can be safely changed (mutable).
     * 
     * @param block
     * @return data options (mutable)
     */
    public final BlockRenderOptions getRenderOptions(Block block) {
        return getRenderOptions(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * Gets a mapping of key:value pairs for all the data-encoded render options set for this BlockData.
     * For example, stairs will add the <i>facing: west</i> key-value pair.
     * The returned object can be safely changed (mutable).
     * 
     * @param world the block is at
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return data options (mutable)
     */
    public abstract BlockRenderOptions getRenderOptions(World world, int x, int y, int z);

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
    public abstract org.bukkit.Material getType();

    /**
     * Gets the legacy Material type of the Block.
     * If your plugin uses an older pre-1.13 API, use this method instead of
     * {@link #getType()}.
     * 
     * @return Block Material Type
     */
    public abstract org.bukkit.Material getLegacyType();

    /**
     * Creates an ItemStack holding a stack of blocks of this type of BlockData.
     * 
     * @param amount of blocks
     * @return Item
     */
    public abstract org.bukkit.inventory.ItemStack createItem(int amount);

    /**
     * Changes a state of this BlockData, returning the BlockData with the state updated.
     * 
     * @param key of the state
     * @param value to set the state to
     * @return updated block data
     */
    public abstract BlockData setState(String key, Object value);

    /**
     * Changes a state of this BlockData, returning the BlockData with the state updated.
     * 
     * @param state the state to change
     * @param value to set the state to
     * @return updated block data
     */
    public abstract BlockData setState(BlockState<?> state, Object value);

    /**
     * Reads a state from this BlockData
     * 
     * @param key of the state
     * @param type to turn the state value into (auto conversion)
     * @return state value
     */
    public abstract <T> T getState(String key, Class<T> type);

    /**
     * Reads a state from this BlockData
     * 
     * @param state the state to get
     * @return state value
     */
    public abstract <T extends Comparable<?>> T getState(BlockState<T> state);

    /**
     * Gets a mapping of all possible block states of this block, and their
     * current state value.
     * 
     * @return block states map
     */
    public abstract Map<BlockState<?>, Comparable<?>> getStates();

    /**
     * Gets whether this BlockData is of a certain Material type.
     * This method keeps legacy and non-legacy materials into account.
     * 
     * @param type to check
     * @return True if matches
     */
    public final boolean isType(org.bukkit.Material type) {
        if (MaterialUtil.isLegacyType(type)) {
            return getLegacyType() == type;
        } else {
            return getType() == type;
        }
    }

    /**
     * Gets whether this BlockData is one of the provided Material types.
     * This method keeps legacy and non-legacy materials into account.
     * 
     * @param type to check
     * @return True if matches
     */
    public final boolean isType(org.bukkit.Material... types) {
        for (org.bukkit.Material type : types) {
            if (isType(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new MaterialData instance appropriate for this Block
     * 
     * @return new Block Material Data <b>(mutable)</b>
     */
    public final MaterialData newMaterialData() {
        return this.getMaterialData().clone();
    }

    /**
     * Gets the MaterialData information for this Block.
     * This Object is not mutable and should not be changed!
     * Read-only access only.
     * 
     * @return material data <b>(immutable)</b>
     */
    public abstract MaterialData getMaterialData();

    /**
     * Gets the facing direction for this Block Data.
     * If this Block is directional, the result of {@link Directional#getFacing()} is returned.
     * If it is not, by default {@link BlockFace#NORTH} is returned.
     * 
     * @return facing direction
     */
    public BlockFace getFacingDirection() {
        MaterialData data = this.getMaterialData();
        if (data instanceof Directional) {
            return ((Directional) data).getFacing();
        } else {
            return BlockFace.NORTH;
        }
    }

    /**
     * Gets the attached face for this BlockData.
     * If this Block is attachable, the result of {@link Attachable#getFacing()} is returned.
     * If it is not, by default {@link BlockFace#DOWN} is returned.
     * 
     * @return attached face
     */
    public BlockFace getAttachedFace() {
        MaterialData data = this.getMaterialData();
        if (data instanceof Attachable) {
            return ((Attachable) data).getAttachedFace();
        } else {
            return BlockFace.DOWN;
        }
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
     * Gets the name of the sound effect played when stepping on this Block
     * 
     * @return step sound
     */
    public abstract ResourceKey getStepSound();

    /**
     * Gets the name of the sound effect played when placing down this Block
     * 
     * @return place sound
     */
    public abstract ResourceKey<SoundEffect> getPlaceSound();

    /**
     * Gets the name of the sound effect played when breaking this Block
     * 
     * @return break sound
     */
    public abstract ResourceKey<SoundEffect> getBreakSound();

    /**
     * Gets the opacity of the Block, a value between 0 and 15.
     * A value of 255 indicates full opaque-ness. A value of 0 is fully transparent.
     *
     * @param world the Block is in
     * @param x world coordinate of the Block
     * @param y world coordinate of the Block
     * @param z world coordinate of the Block
     * @return the opacity
     */
    public abstract int getOpacity(World world, int x, int y, int z);

    /**
     * Gets the opacity of the Block, a value between 0 and 15.
     * A value of 255 indicates full opaque-ness. A value of 0 is fully transparent.
     *
     * @param block
     * @return the opacity
     */
    public int getOpacity(Block block) {
        return getOpacity(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * Gets the amount of light the Block radiates, value between 0 and 15.
     *
     * @return light emission
     */
    public abstract int getEmission();

    /**
     * Gets whether this Block is fully occluding, not allowing light to pass through
     * 
     * @param block where this BlockData is used
     * @return True if fully occluding, False if not
     */
    public abstract boolean isOccluding(Block block);

    /**
     * Gets whether this Block is fully occluding, not allowing light to pass through
     * 
     * @param world where this BlockData is used
     * @param x - coordinate of the block where this BlockData is used
     * @param y - coordinate of the block where this BlockData is used
     * @param z - coordinate of the block where this BlockData is used
     * @return True if fully occluding, False if not
     */
    public abstract boolean isOccluding(World world, int x, int y, int z);

    /**
     * Gets whether this Block causes suffocation damage to players inside
     * 
     * @param block where this BlockData is used
     * @return True if suffocation occurs, False if not
     */
    public abstract boolean isSuffocating(Block block);

    /**
     * Gets whether this Block is a source of Redstone Power
     * 
     * @return True if the block is a power source, False if not
     */
    public abstract boolean isPowerSource();

    /**
     * Gets whether this Block can support other attachable blocks, like signs
     * 
     * @return True if buildable
     */
    public abstract boolean isBuildable();

    /**
     * Gets the damage resilience of a block to damage dealt
     *
     * @return resilience value
     */
    public abstract float getDamageResilience();

    /**
     * Gets whether this Block is capable of supporting other blocks on top, like torches and minecart track.
     * 
     * @param block position where this BlockData exists
     * @return True if supporting blocks on top
     */
    public abstract boolean canSupportTop(Block block);

    /**
     * Gets the bounding box of a block of this Block Data type/
     * For convenience it is better to use {@link BlockUtil#getBoundingBox} instead.
     * 
     * @param block
     * @return bounding box of the block (relative to block coordinates)
     */
    public abstract AxisAlignedBBHandle getBoundingBox(Block block);

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
