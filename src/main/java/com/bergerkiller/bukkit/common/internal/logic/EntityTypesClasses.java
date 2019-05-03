package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;

public abstract class EntityTypesClasses {
    private static final EntityTypesClasses INSTANCE;

    static {
        if (Common.evaluateMCVersion(">=", "1.14")) {
            INSTANCE = new EntityTypesClasses_1_14();
        } else if (Common.evaluateMCVersion(">=", "1.13")) {
            INSTANCE = new EntityTypesClasses_1_13();
        } else {
            INSTANCE = null; // Unused
        }
    }

    /**
     * Obtains the Class of the entity that spawns for an EntityTypes instance.
     * Implementation callback.
     * 
     * @param nmsEntityTypesInstance
     * @return NMS Entity Class
     */
    public abstract Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance);

    /**
     * Obtains the Class of the entity that spawns for an EntityTypes instance.
     * 
     * @param nmsEntityTypesInstance
     * @return NMS Entity Class
     */
    public static Class<?> get(Object nmsEntityTypesInstance) {
        return INSTANCE.getClassFromEntityTypes(nmsEntityTypesInstance);
    }
}
