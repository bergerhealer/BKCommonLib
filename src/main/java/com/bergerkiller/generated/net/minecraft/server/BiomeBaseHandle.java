package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.WeightedRandomHandle.WeightedRandomChoiceHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BiomeBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BiomeBaseHandle extends Template.Handle {
    /** @See {@link BiomeBaseClass} */
    public static final BiomeBaseClass T = new BiomeBaseClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BiomeBaseHandle.class, "net.minecraft.server.BiomeBase");

    /* ============================================================================== */

    public static BiomeBaseHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
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
    public abstract static class BiomeMetaHandle extends WeightedRandomChoiceHandle {
        /** @See {@link BiomeMetaClass} */
        public static final BiomeMetaClass T = new BiomeMetaClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(BiomeMetaHandle.class, "net.minecraft.server.BiomeBase.BiomeMeta");

        /* ============================================================================== */

        public static BiomeMetaHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        public static final BiomeMetaHandle createNew(java.lang.Class<?> entityClass, int x, int y, int z) {
            return T.constr_entityClass_x_y_z.newInstance(entityClass, x, y, z);
        }

        /* ============================================================================== */

        public abstract java.lang.Class<?> getEntityClass();
        public abstract void setEntityClass(java.lang.Class<?> value);
        public abstract int getMinSpawnCount();
        public abstract void setMinSpawnCount(int value);
        public abstract int getMaxSpawnCount();
        public abstract void setMaxSpawnCount(int value);
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

