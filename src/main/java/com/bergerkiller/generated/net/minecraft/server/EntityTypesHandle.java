package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityTypes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityTypesHandle extends Template.Handle {
    /** @See {@link EntityTypesClass} */
    public static final EntityTypesClass T = new EntityTypesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTypesHandle.class, "net.minecraft.server.EntityTypes");

    public static final RegistryMaterialsHandle opt_registry = T.opt_registry.getSafe();
    /* ============================================================================== */

    public static EntityTypesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Class<?> getEntityClass(String internalEntityName) {
        return T.getEntityClass.invoke(internalEntityName);
    }

    public static String getEntityInternalName(Class<?> entityType) {
        return T.getEntityInternalName.invoke(entityType);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityTypes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTypesClass extends Template.Class<EntityTypesHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Map<String, Class<?>>> opt_nameTypeMap_1_10_2 = new Template.StaticField.Converted<Map<String, Class<?>>>();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, String>> opt_typeNameMap_1_10_2 = new Template.StaticField.Converted<Map<Class<?>, String>>();
        public final Template.StaticField.Converted<RegistryMaterialsHandle> opt_registry = new Template.StaticField.Converted<RegistryMaterialsHandle>();

        @Template.Optional
        public final Template.StaticMethod.Converted<EntityTypesHandle> fromEntityClass = new Template.StaticMethod.Converted<EntityTypesHandle>();
        public final Template.StaticMethod<Class<?>> getEntityClass = new Template.StaticMethod<Class<?>>();
        public final Template.StaticMethod<String> getEntityInternalName = new Template.StaticMethod<String>();

        @Template.Optional
        public final Template.Method.Converted<Class<?>> getEntityClassInst = new Template.Method.Converted<Class<?>>();

    }

}

