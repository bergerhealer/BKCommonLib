package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSEntityTypes {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTypes");

    public static final FieldAccessor<Object> entityMapping = T.selectField("public static final RegistryMaterials<MinecraftKey, Class<? extends Entity>> b");

    /**
     * Registers a new entity
     */
    public static void register(int entityId, String entityKey, Class<?> entityClass, String entityName) {
        EntityTypesHandle.register(entityId, entityKey, entityClass, entityName);
    }

    /**
     * Gets the Minecraft key associated with an Entity Class Type
     * 
     * @param type of Entity
     * @return Minecraft key
     */
    public static Object getName(Class<?> type) {
        return EntityTypesHandle.getName(type);
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
