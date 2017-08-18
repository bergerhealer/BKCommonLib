package com.bergerkiller.bukkit.common.internal;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * Keeps track of map UUID <> Map Id conversion
 */
public class CommonMapUUIDStore {

    /**
     * Internal use only! Obtains the unique Id of a map item. Returns -1 when the item is not a valid map.
     * This function may be subject to change and should not be depended on.
     * 
     * @param item to get the Map Id for
     * @return map id
     */
    public static UUID getMapUUID(ItemStack item) {
        if (item == null || item.getType() != Material.MAP) {
            return null;
        } else {
            CommonTagCompound tag = ItemUtil.getMetaTag(item, false);
            if (tag != null) {
                UUID uuid = tag.getUUID("mapDisplay");
                if (uuid != null) {
                    return uuid;
                }
            }

            // Turn Map durability into an UUID
            return getStaticMapUUID(item.getDurability());
        }
    }

    /**
     * Internal use only! Obtains a UUID from a map durability short value.
     * 
     * @param mapId to turn into a UUID
     * @return map UUID
     */
    public static UUID getStaticMapUUID(short mapId) {
        // Turn Map durability into an UUID
        long leastBits = (long) mapId;
        long mostBits = 0L;
        return new UUID(mostBits, leastBits);
    }

    /**
     * Internal use only! If the UUID is that of a static map UUID, returns
     * the static Map Id. Returns -1 if the UUID is not static.
     * 
     * @param uuid
     * @return map Id, -1 if not static
     */
    public static short getStaticMapId(UUID uuid) {
        if (uuid.getMostSignificantBits() != 0L) {
            return -1;
        }
        long least = uuid.getLeastSignificantBits();
        short id = (short) (least & 0xFFFFL);
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
        } while (getStaticMapId(uuid) != -1);
        return uuid;
    }
}
