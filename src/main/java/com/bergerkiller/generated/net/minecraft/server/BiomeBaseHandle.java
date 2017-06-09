package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.WeightedRandomHandle.WeightedRandomChoiceHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BiomeBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class BiomeBaseHandle extends Template.Handle {
    /** @See {@link BiomeBaseClass} */
    public static final BiomeBaseClass T = new BiomeBaseClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BiomeBaseHandle.class, "net.minecraft.server.BiomeBase");

    /* ============================================================================== */

    public static BiomeBaseHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BiomeBaseHandle handle = new BiomeBaseHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.BiomeBase</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BiomeBaseClass extends Template.Class<BiomeBaseHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.BiomeBase.BiomeMeta</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class BiomeMetaHandle extends WeightedRandomChoiceHandle {
        /** @See {@link BiomeMetaClass} */
        public static final BiomeMetaClass T = new BiomeMetaClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(BiomeMetaHandle.class, "net.minecraft.server.BiomeBase.BiomeMeta");

        /* ============================================================================== */

        public static BiomeMetaHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            BiomeMetaHandle handle = new BiomeMetaHandle();
            handle.instance = handleInstance;
            return handle;
        }

        public static final BiomeMetaHandle createNew(java.lang.Class<?> entityClass, int x, int y, int z) {
            return T.constr_entityClass_x_y_z.newInstance(entityClass, x, y, z);
        }

        /* ============================================================================== */

        public java.lang.Class<?> getEntityClass() {
            return T.entityClass.get(instance);
        }

        public void setEntityClass(java.lang.Class<?> value) {
            T.entityClass.set(instance, value);
        }

        public int getMinSpawnCount() {
            return T.minSpawnCount.getInteger(instance);
        }

        public void setMinSpawnCount(int value) {
            T.minSpawnCount.setInteger(instance, value);
        }

        public int getMaxSpawnCount() {
            return T.maxSpawnCount.getInteger(instance);
        }

        public void setMaxSpawnCount(int value) {
            T.maxSpawnCount.setInteger(instance, value);
        }

        /**
         * Stores class members for <b>net.minecraft.server.BiomeBase.BiomeMeta</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class BiomeMetaClass extends Template.Class<BiomeMetaHandle> {
            public final Template.Constructor.Converted<BiomeMetaHandle> constr_entityClass_x_y_z = new Template.Constructor.Converted<BiomeMetaHandle>();

            public final Template.Field.Converted<java.lang.Class<?>> entityClass = new Template.Field.Converted<java.lang.Class<?>>();
            public final Template.Field.Integer minSpawnCount = new Template.Field.Integer();
            public final Template.Field.Integer maxSpawnCount = new Template.Field.Integer();

        }

    }

}

