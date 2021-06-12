package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

@Deprecated
public class NMSEntityTypes {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTypes");

    /**
     * Retrieves the class belonging to an Entity, by name.
     * Returns null if the entity could not be found.
     * 
     * @param entityName to look up
     * @return Entity Class for the Entity registered to this name
     */
    public static Class<?> getEntityClass(String entityName) {
        return EntityTypesHandle.getEntityClass(entityName);
    }

    /**
     * Gets the Minecraft key associated with an Entity Class Type
     * 
     * @param type of Entity
     * @return Minecraft key
     */
    public static String getName(Class<?> type) {
        return EntityTypesHandle.getEntityInternalName(type);
    }

    /**
     * Unregisters a registered entity
     *
     * @param entityClass to unregister
     */
    //TODO: BROKEN!!!!
    /*
    public static void unregister(Class<?> entityClass) {
        String name = classToNames.remove(entityClass);
        if (name != null) {
            namesToClass.remove(name);
            namesToId.remove(name);
        }
        Integer id = classToId.remove(entityClass);
        if (id != null) {
            idToClass.remove(id);
        }
    }
    */
}
