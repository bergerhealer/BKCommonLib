package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.HashMap;
import java.util.Map;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;

/**
 * TileEntityTypes class/registry was added in Minecraft 1.13.
 * This proxy class acts as an intermediate for the time it does not
 * exist
 */
public class TileEntityTypesProxy_1_8_to_1_12_2 {
    private static final Map<Integer, TileEntityTypesProxy_1_8_to_1_12_2> byId = new HashMap<>();
    private static final Map<Object, TileEntityTypesProxy_1_8_to_1_12_2> byKey = new HashMap<>();
    public final int id;
    public final Object minecraftKey;

    static {
        for (Map.Entry<Integer, Object> entry : TileEntityTypesSerializedIds_1_8_to_1_17_1.allEntries()) {
            TileEntityTypesProxy_1_8_to_1_12_2 value = new TileEntityTypesProxy_1_8_to_1_12_2(
                    entry.getKey(), entry.getValue());
            byId.put(value.id, value);
            byKey.put(value.minecraftKey, value);
        }
    }

    private TileEntityTypesProxy_1_8_to_1_12_2(int id, Object minecraftKey) {
        this.id = id;
        this.minecraftKey = minecraftKey;
    }

    public static TileEntityTypesProxy_1_8_to_1_12_2 byId(int id) {
        TileEntityTypesProxy_1_8_to_1_12_2 value = byId.get(id);
        if (value == null) {
            Object minecraftKey = MinecraftKeyHandle.createNew("UNKNOWN_TILE_ID_" + id).getRaw();
            value = new TileEntityTypesProxy_1_8_to_1_12_2(id, minecraftKey);
        }
        return value;
    }

    public static TileEntityTypesProxy_1_8_to_1_12_2 byKey(Object nmsMinecraftKeyHandle) {
        TileEntityTypesProxy_1_8_to_1_12_2 value = byKey.get(nmsMinecraftKeyHandle);
        if (value == null) {
            value = new TileEntityTypesProxy_1_8_to_1_12_2(-1, nmsMinecraftKeyHandle);
        }
        return value;
    }
}
