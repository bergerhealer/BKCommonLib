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

    /* ============================================================================== */

    public static EntityTypesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.EntityTypes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTypesClass extends Template.Class<EntityTypesHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Object> opt_entityRegistry = new Template.StaticField.Converted<Object>();
        @Template.Optional
        public final Template.StaticField.Converted<Map<String, Class<?>>> opt_entityMap = new Template.StaticField.Converted<Map<String, Class<?>>>();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, String>> entityNamesMap_1_8_8 = new Template.StaticField.Converted<Map<Class<?>, String>>();

        @Template.Optional
        public final Template.StaticMethod.Converted<Void> register = new Template.StaticMethod.Converted<Void>();
        @Template.Optional
        public final Template.StaticMethod.Converted<Void> register_old = new Template.StaticMethod.Converted<Void>();
        @Template.Optional
        public final Template.StaticMethod.Converted<Object> getName = new Template.StaticMethod.Converted<Object>();
        @Template.Optional
        public final Template.StaticMethod.Converted<String> getName_old = new Template.StaticMethod.Converted<String>();

    }

}

