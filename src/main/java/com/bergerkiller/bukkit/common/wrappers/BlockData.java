package com.bergerkiller.bukkit.common.wrappers;

import java.util.Map;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

/**
 * Stores a reference to net.minecraft.world.level.block.Block and IBlockData objects,
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
     * @param materialdata Material and legacy data to load
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
     * {@link #getType()}.<br>
     * <br>
     * Will return LEGACY_AIR for block data that has no legacy type,
     * according to {@link #hasLegacyType()}.
     * 
     * @return Block Material Type
     */
    public abstract org.bukkit.Material getLegacyType();

    /**
     * Gets whether this BlockData has a valid, existing legacy Material type
     * that is exactly the same. For example, BlockData of OAK_PLANKS will match
     * the LEGACY_WOOD type, and as such, has a legacy type (=true). But WARPED_PLANKS
     * lacks any legacy type because it was added to the game after the material
     * api changes (=false)
     *
     * @return True if this BlockData has a valid legacy type
     */
    public abstract boolean hasLegacyType();

    /**
     * Creates an ItemStack holding a stack of blocks of this type of BlockData.
     * 
     * @param amount of blocks
     * @return Bukkit ItemStack
     */
    public abstract org.bukkit.inventory.ItemStack createItem(int amount);

    /**
     * Creates a CommonItemStack holding a stack of blocks of this type of BlockData.
     *
     * @param amount of blocks
     * @return CommonItemStack
     */
    public final CommonItemStack createCommonItem(int amount) {
        return CommonItemStack.of(createItem(amount));
    }

    /**
     * Changes a state of this BlockData, returning the BlockData with the state updated.
     * 
     * @param key Name of the state to change
     * @param value to set the state to
     * @return updated block data
     */
    public abstract BlockData setState(String key, Object value);

    /**
     * Changes a state of this BlockData, returning the BlockData with the state updated.
     * 
     * @param stateKey Key of the state to change
     * @param value to set the state to
     * @return updated block data
     */
    public abstract BlockData setState(BlockDataStateKey<?> stateKey, Object value);

    /**
     * @deprecated Use {@link #setState(BlockDataStateKey, Object)} instead
     */
    @Deprecated
    public abstract BlockData setState(BlockState<?> stateKey, Object value);

    /**
     * Reads a state from this BlockData
     * 
     * @param key Name of the state to get
     * @param type to turn the state value into (auto conversion)
     * @return state value
     */
    public abstract <T> T getState(String key, Class<T> type);

    /**
     * Reads a state from this BlockData
     * 
     * @param stateKey Key of the state to get
     * @return state value
     */
    public abstract <T extends Comparable<?>> T getState(BlockDataStateKey<T> stateKey);

    /**
     * Gets a mapping of all possible block states of this block, and their
     * current state value.
     *
     * @return block states map
     */
    public abstract Map<BlockDataStateKey<?>, Comparable<?>> getStates();

    /**
     * Gets the state key that can be used to update a BlockState using
     * {@link #setState(BlockDataStateKey, Object)} or get it using
     * {@link #getState(BlockDataStateKey)}
     *
     * @param key Name of the state
     * @return BlockState key, or <i>null</i> if not found
     */
    public abstract BlockDataStateKey<?> getStateKey(String key);

    /**
     * Gets whether this BlockData is of a certain Material type.
     * This method keeps legacy and non-legacy materials into account.
     * 
     * @param type to check
     * @return True if matches
     */
    public final boolean isType(org.bukkit.Material type) {
        if (MaterialUtil.isLegacyType(type)) {
            return this.getLegacyType() == type && this.hasLegacyType();
        } else {
            return getType() == type;
        }
    }

    /**
     * Gets whether this BlockData is one of the provided Material types.
     * This method keeps legacy and non-legacy materials into account.
     * 
     * @param types Material types to check
     * @return True if any of the types match
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
        return LogicUtil.tryCast(newMaterialData(), type);
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    /**
     * Gets the name of the sound effect played when stepping on this Block
     * 
     * @return step sound
     */
    public abstract ResourceKey<SoundEffect> getStepSound();

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
     * @see #getOpaqueFaces(Block) 
     */
    public abstract int getOpacity(Block block);

    /**
     * Gets a BlockFaceSet of all the faces of this block that are opaque.
     * Opaque faces do not let light through.
     * 
     * @param world the Block is in
     * @param x world coordinate of the Block
     * @param y world coordinate of the Block
     * @param z world coordinate of the Block
     * @return BlockFaceSet of opaque faces
     */
    public abstract BlockFaceSet getOpaqueFaces(World world, int x, int y, int z);

    /**
     * Gets a BlockFaceSet of all the faces of this block that are opaque.
     * Opaque faces do not let light through.
     *
     * @param block
     * @return the opacity
     */
    public abstract BlockFaceSet getOpaqueFaces(Block block);

    /**
     * Gets the amount of light the Block radiates, value between 0 and 15.
     *
     * @return light emission
     * @deprecated Does not work with forge, which requires block information to be passed
     */
    @Deprecated
    public abstract int getEmission();

    /**
     * Gets the amount of light the Block radiates, value between 0 and 15.
     *
     * @param world The world the block is in
     * @param x The X-coordinate of the block
     * @param y The Y-coordinate of the block
     * @param z The Z-coordinate of the block
     * @return light emission
     */
    public abstract int getEmission(World world, int x, int y, int z);

    /**
     * Gets the amount of light the Block radiates, value between 0 and 15.
     *
     * @param block The block to get emission of
     * @return light emission
     */
    public abstract int getEmission(Block block);

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
     * @deprecated Same as {@link #isSolid()}
     */
    public final boolean isBuildable() {
        return isSolid();
    }

    /**
     * Gets whether this Block can support other attachable blocks, like signs
     * 
     * @return True if buildable
     */
    public abstract boolean isSolid();

    /**
     * Gets the damage resilience of a block to damage dealt
     *
     * @return resilience value
     */
    public abstract float getDamageResilience();

    /**
     * Gets whether this Block is capable of supporting other blocks on top, like torches and minecart track.
     * Equivalent to {@link #canSupportOnFace(Block, BlockFace)} with UP as face.
     * 
     * @param block position where this BlockData exists
     * @return True if supporting blocks on top
     */
    public abstract boolean canSupportTop(Block block);

    /**
     * Gets whether the given block face of a Block is capable of supporting other blocks, like torches and minecart track.
     * 
     * @param block position where this BlockData exists
     * @param face BlockFace to check
     * @return True if supporting blocks on this face
     */
    public abstract boolean canSupportOnFace(Block block, BlockFace face);

    /**
     * Gets the bounding box of a block of this Block Data type.
     * For convenience it is better to use
     * {@link com.bergerkiller.bukkit.common.utils.BlockUtil#getBoundingBox(Block)
     *        BlockUtil.getBoundingBox(Block)}
     * instead.
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
