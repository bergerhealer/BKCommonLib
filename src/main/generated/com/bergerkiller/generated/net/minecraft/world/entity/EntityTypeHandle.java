package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import java.util.List;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EntityType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.EntityType")
public abstract class EntityTypeHandle extends Template.Handle {
    /** @see EntityTypeClass */
    public static final EntityTypeClass T = Template.Class.create(EntityTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Class<?> getEntityClass(String internalEntityName) {
        return T.getEntityClass.invoker.invoke(null,internalEntityName);
    }

    public static String getEntityInternalName(Class<?> entityType) {
        return T.getEntityInternalName.invoker.invoke(null,entityType);
    }

    public static int getEntityTypeId(Class<?> entityType) {
        return T.getEntityTypeId.invoker.invoke(null,entityType);
    }

    /**
     * Stores class members for <b>net.minecraft.world.entity.EntityType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTypeClass extends Template.Class<EntityTypeHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Map<String, Class<?>>> opt_nameTypeMap_1_10_2 = new Template.StaticField.Converted<Map<String, Class<?>>>();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, String>> opt_typeNameMap_1_10_2 = new Template.StaticField.Converted<Map<Class<?>, String>>();
        @Template.Optional
        public final Template.StaticField<List<String>> opt_typeIdToName_1_11 = new Template.StaticField<List<String>>();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, Integer>> opt_typeIdMap_1_8 = new Template.StaticField.Converted<Map<Class<?>, Integer>>();

        @Template.Optional
        public final Template.StaticMethod.Converted<MappedRegistryHandle> opt_getRegistry = new Template.StaticMethod.Converted<MappedRegistryHandle>();
        @Template.Optional
        public final Template.StaticMethod.Converted<EntityTypeHandle> fromEntityClass = new Template.StaticMethod.Converted<EntityTypeHandle>();
        public final Template.StaticMethod<Class<?>> getEntityClass = new Template.StaticMethod<Class<?>>();
        public final Template.StaticMethod<String> getEntityInternalName = new Template.StaticMethod<String>();
        public final Template.StaticMethod<Integer> getEntityTypeId = new Template.StaticMethod<Integer>();

        @Template.Optional
        public final Template.Method.Converted<Class<?>> getEntityClassInst = new Template.Method.Converted<Class<?>>();
        @Template.Optional
        public final Template.Method<Integer> getTypeId = new Template.Method<Integer>();

    }

}

