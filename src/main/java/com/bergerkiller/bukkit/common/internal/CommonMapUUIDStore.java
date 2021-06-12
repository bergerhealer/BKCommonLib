package com.bergerkiller.bukkit.common.internal;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;

/**
 * Keeps track of map UUID <> Map Id conversion
 */
public class CommonMapUUIDStore {
    /**
     * Material for a filled map, the type given out when creating map display items
     */
    public static final Material FILLED_MAP_TYPE;

    static {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            FILLED_MAP_TYPE = CommonLegacyMaterials.getMaterial("FILLED_MAP");
        } else {
            FILLED_MAP_TYPE = CommonLegacyMaterials.getLegacyMaterial("MAP");
        }
    }

    /**
     * Gets whether an ItemStack contains a Map item
     * 
     * @param item
     * @return True if a map item
     */
    public static boolean isMap(ItemStack item) {
        return item != null && item.getType() == FILLED_MAP_TYPE;
    }

    /**
     * Gets the Map Id stored inside a Map Item
     * 
     * @param item to get it for (must be a CraftItemStack, can not be null)
     * @return map Id
     */
    public static int getItemMapId(ItemStack item) {
        return ItemStackHandle.T.getMapId.invoke(CraftItemStackHandle.T.handle.raw.get(item)).intValue();
    }

    /**
     * Sets the Map Id stored inside a Map Item
     * 
     * @param item to set it for (must be a CraftItemStack, can not be null)
     * @param mapId to set to
     */
    public static void setItemMapId(ItemStack item, int mapId) {
        ItemStackHandle.T.setMapId.invoke(CraftItemStackHandle.T.handle.raw.get(item), mapId);
    }

    /**
     * Internal use only! Obtains the unique Id of a map item. Returns null when the item is not a valid map.
     * This function may be subject to change and should not be depended on.
     * 
     * @param item to get the Map Id for (must be a CraftItemStack)
     * @return map id
     */
    public static UUID getMapUUID(ItemStack item) {
        if (item == null) {
            return null;
        }
        Object nmsItemStack = CraftItemStackHandle.T.handle.raw.get(item);
        if (nmsItemStack == null) {
            return null;
        }
        return ItemStackHandle.T.getMapDisplayUUID.invoke(nmsItemStack);
    }

    /**
     * Internal use only! Obtains a UUID from a map id value.
     * 
     * @param mapId to turn into a UUID
     * @return map UUID
     */
    public static UUID getStaticMapUUID(int mapId) {
        // Turn Map durability into an UUID
        long leastBits = (long) mapId;
        long mostBits = 0L;
        return new UUID(mostBits, leastBits);
    }

    /**
     * Internal use only! Gets whether the UUID of a map is that of a static
     * id.
     * 
     * @param uuid
     * @return True if static
     */
    public static boolean isStaticMapId(UUID uuid) {
        return uuid.getMostSignificantBits() == 0 &&
               (uuid.getLeastSignificantBits() & 0xFFFFFFFF00000000L) == 0;
    }

    /**
     * Internal use only! If the UUID is that of a static map UUID, returns
     * the static Map Id. Returns -1 if the UUID is not static.
     * 
     * @param uuid
     * @return map Id, -1 if not static
     */
    public static int getStaticMapId(UUID uuid) {
        if (uuid.getMostSignificantBits() != 0L) {
            return -1;
        }
        long least = uuid.getLeastSignificantBits();
        int id = (int) (least & 0xFFFFFFFFL);
        if ((least - id) != 0L) {
            return -1;
        }
        return id;
    }

    /**
     * Generates a new Map UUID for uniquely identifying a map item instance
     * 
     * @return new map instance UUID
     */
    public static UUID generateDynamicMapUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (isStaticMapId(uuid));
        return uuid;
    }
}
