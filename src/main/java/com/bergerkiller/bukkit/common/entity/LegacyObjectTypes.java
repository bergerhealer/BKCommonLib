package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores a registry of spawn packet object type ids before the API for this was available.
 * Only used on Minecraft 1.13.2 and before, since 1.14 this has an NMS API we can call into.
 */
class LegacyObjectTypes {
    private static final Map<String, ObjectTypeInfo> objectTypes = new HashMap<>();

    static {
        registerObjectType("BOAT", 1, -1);
        registerObjectType("DROPPED_ITEM", 2, 1);
        registerObjectType("AREA_EFFECT_CLOUD", 3, -1);
        registerObjectType("MINECART", 10, 0);
        registerObjectType("MINECART_CHEST", 10, 1);
        registerObjectType("MINECART_FURNACE", 10, 2);
        registerObjectType("MINECART_TNT", 10, 3);
        registerObjectType("MINECART_MOB_SPAWNER", 10, 4);
        registerObjectType("MINECART_HOPPER", 10, 5);
        registerObjectType("MINECART_COMMAND", 10, 6);
        registerObjectType("PRIMED_TNT", 50, -1);
        registerObjectType("ENDER_CRYSTAL", 51, -1);
        registerObjectType("ARROW", 60, -1);
        registerObjectType("TIPPED_ARROW", 60, -1);
        registerObjectType("SNOWBALL", 61, -1);
        registerObjectType("EGG", 62, -1);
        registerObjectType("FIREBALL", 63, -1);
        registerObjectType("SMALL_FIREBALL", 64, -1);
        registerObjectType("ENDER_PEARL", 65, -1);
        registerObjectType("WITHER_SKULL", 66, -1);
        registerObjectType("SHULKER_BULLET", 67, 0);
        registerObjectType("LLAMA_SPIT", 68, 0);
        registerObjectType("FALLING_BLOCK", 70, -1);
        registerObjectType("ITEM_FRAME", 71, -1);
        registerObjectType("ENDER_SIGNAL", 72, -1);
        registerObjectType("LINGERING_POTION", 73, 0);
        registerObjectType("SPLASH_POTION", 73, 0);
        registerObjectType("THROWN_EXP_BOTTLE", 75, 0);
        registerObjectType("FIREWORK", 76, -1);
        registerObjectType("LEASH_HITCH", 77, -1);
        registerObjectType("ARMOR_STAND", 78, -1);
        registerObjectType("EVOKER_FANGS", 79, -1);
        registerObjectType("FISHING_HOOK", 90, -1);
        registerObjectType("SPECTRAL_ARROW", 91, -1);
        registerObjectType("DRAGON_FIREBALL", 92, -1);
    }

    public static ObjectTypeInfo find(EntityType entityType) {
        return (entityType == null) ? null : objectTypes.get(entityType.name());
    }

    private static void registerObjectType(String entityTypeName, int objectId, int extraData) {
        ObjectTypeInfo info = new ObjectTypeInfo();
        info.typeId = objectId;
        info.extraData = extraData;
        objectTypes.put(entityTypeName, info);
    }

    public static class ObjectTypeInfo {
        public int typeId;
        public int extraData;
    }
}
