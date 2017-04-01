package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSEntityTypes {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTypes");

    public static final FieldAccessor<Object> entityMapping = T.selectField("public static final RegistryMaterials<MinecraftKey, Class<? extends Entity>> b");

    private static final MethodAccessor<Void> register = T.selectMethod("private static void a(int entityId, String name1, Class<? extends Entity> entityClass, String name2)");

    /**
     * Registers a new entity
     */
    public static void register(int entityId, String entityKey, Class<?> entityClass, String entityName) {
        register.invoke(null, entityId, entityKey, entityClass, entityName);
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
