package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BiomeBaseHandle extends Template.Handle {
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

    public static final class BiomeBaseClass extends Template.Class<BiomeBaseHandle> {
    }

    public static class BiomeMetaHandle extends Template.Handle {
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

        public static final class BiomeMetaClass extends Template.Class<BiomeMetaHandle> {
            public final Template.Constructor.Converted<BiomeMetaHandle> constr_entityClass_x_y_z = new Template.Constructor.Converted<BiomeMetaHandle>();

            public final Template.Field.Converted<java.lang.Class<?>> entityClass = new Template.Field.Converted<java.lang.Class<?>>();

        }
    }
}
