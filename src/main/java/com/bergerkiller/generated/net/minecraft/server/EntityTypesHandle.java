package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityTypes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityTypesHandle extends Template.Handle {
    /** @See {@link EntityTypesClass} */
    public static final EntityTypesClass T = new EntityTypesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTypesHandle.class, "net.minecraft.server.EntityTypes");

    /* ============================================================================== */

    public static EntityTypesHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityTypesHandle handle = new EntityTypesHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static void register(int entityId, String name1, Class<?> entityClass, String name2) {
        T.register.invokeVA(entityId, name1, entityClass, name2);
    }

    public static Object getName(Class<?> paramClass) {
        return T.getName.invokeVA(paramClass);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityTypes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTypesClass extends Template.Class<EntityTypesHandle> {
        public final Template.StaticMethod.Converted<Void> register = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<Object> getName = new Template.StaticMethod.Converted<Object>();

    }

}

