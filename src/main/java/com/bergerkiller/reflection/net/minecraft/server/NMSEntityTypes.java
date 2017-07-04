package com.bergerkiller.reflection.net.minecraft.server;

import java.util.Map;

import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

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
        if (EntityTypesHandle.T.opt_entityRegistry.isAvailable()) {
            Object entityRegistry = EntityTypesHandle.T.opt_entityRegistry.get();
            if (entityRegistry == null) {
                throw new UnsupportedOperationException("Entity Registry is unavailable (null)");
            }
            Object mcKey = NMSMinecraftKey.newInstance(entityName);
            return (Class<?>) NMSRegistryMaterials.getValue.invoke(entityRegistry, mcKey);
        } else if (EntityTypesHandle.T.opt_entityMap.isAvailable()) {
            Map<String, Class<?>> entityMap = EntityTypesHandle.T.opt_entityMap.get();
            if (entityMap == null) {
                throw new UnsupportedOperationException("Entity Map is unavailable (null)");
            }
            return entityMap.get(entityName);
        } else {
            throw new UnsupportedOperationException("No Entity Mapping registry fields could be detected");
        }
    }

    /**
     * Registers a new entity
     */
    public static void register(int entityId, String entityKey, Class<?> entityClass, String entityName) {
        if (EntityTypesHandle.T.register.isAvailable()) {
            EntityTypesHandle.T.register.invoke(entityId, entityKey, entityClass, entityName);
        } else if (EntityTypesHandle.T.register_old.isAvailable()) {
            EntityTypesHandle.T.register_old.invoke(entityClass, entityName, entityId);
        } else {
            throw new UnsupportedOperationException("Entity Registration is not supported on this server");
        }
    }

    /**
     * Gets the Minecraft key associated with an Entity Class Type
     * 
     * @param type of Entity
     * @return Minecraft key
     */
    public static String getName(Class<?> type) {
        if (EntityTypesHandle.T.getName.isAvailable()) {
            Object mcKey = EntityTypesHandle.T.getName.invoke(type);
            return (mcKey == null) ? null : mcKey.toString();
        } else if (EntityTypesHandle.T.getName_old.isAvailable()) {
            return EntityTypesHandle.T.getName_old.invoke(type);
        } else {
            throw new UnsupportedOperationException("Entity Name by Class lookup is not supported on this server");
        }
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
