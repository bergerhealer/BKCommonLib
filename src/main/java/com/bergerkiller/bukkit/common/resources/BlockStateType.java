package com.bergerkiller.bukkit.common.resources;

import java.util.IdentityHashMap;
import java.util.Map;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityTypesHandle;

/**
 * A type of block state. Used for Block State change packet listening
 * and protocol API. Each unique type of Block state (Tile entity) has
 * a matching BlockStateType registered.<br>
 * <br>
 * Note: BlockStateType instances are unique, and can be compared using
 * reference equality rather than .equals(). It behaves similar to an enum.
 */
public class BlockStateType extends BasicWrapper<TileEntityTypesHandle> {
    private static final Map<Object, BlockStateType> _cache = new IdentityHashMap<>();
    public static final BlockStateType SIGN = byName("sign");

    private BlockStateType(TileEntityTypesHandle handle) {
        setHandle(handle);
    }

    /**
     * Gets the unique index of this block state type in the registry.
     * Is used as part of the protocol serialization/deserialization.
     *
     * @return Block state type unique serialized ID
     */
    public int getSerializedId() {
        return handle.getId();
    }

    /**
     * Gets the unique name identifier of this block state type in the registry.
     * This is the name of this type, such as "minecraft:sign".
     *
     * @return Block state type key
     */
    public MinecraftKeyHandle getKey() {
        return handle.getKey();
    }

    @Override
    public String toString() {
        return "BlockStateType{" + getKey().toString() + "}";
    }

    /**
     * Looks up a Block State type by the serialized ID
     *
     * @param id Serialized id
     * @return Block state type
     * @see {@link #getSerializedId()}
     */
    public static BlockStateType bySerializedId(int id) {
        return fromTileEntityTypesHandle(TileEntityTypesHandle.getRawById(id));
    }

    /**
     * Looks up a Block State type by the registered name
     *
     * @param name Name, e.g. "sign" or "minecraft:sign"
     * @return Block state type, or null if not registered
     */
    public static BlockStateType byName(String name) {
        return byKey(MinecraftKeyHandle.createNew(name));
    }

    /**
     * Looks up a Block State type by the registered key
     *
     * @param key Minecraft key
     * @return Block state type, or null if not registered
     */
    public static BlockStateType byKey(MinecraftKeyHandle key) {
        return fromTileEntityTypesHandle(TileEntityTypesHandle.getRawByKey(key));
    }

    /**
     * Looks up the Block State type used for representing an internal net.minecraft
     * TileEntityTypes instance.
     *
     * @param nmsTileEntityTypesHandle
     * @return Block state type for this handle, always succeeds. Null if handle is null.
     */
    public static BlockStateType fromTileEntityTypesHandle(Object nmsTileEntityTypesHandle) {
        if (nmsTileEntityTypesHandle == null) {
            return null;
        }

        synchronized (_cache) {
            return _cache.computeIfAbsent(nmsTileEntityTypesHandle,
                    raw -> new BlockStateType(TileEntityTypesHandle.createHandle(raw)));
        }
    }
}
