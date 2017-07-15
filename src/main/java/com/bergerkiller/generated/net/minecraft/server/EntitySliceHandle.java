package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntitySlice</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class EntitySliceHandle extends Template.Handle {
    /** @See {@link EntitySliceClass} */
    public static final EntitySliceClass T = new EntitySliceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntitySliceHandle.class, "net.minecraft.server.EntitySlice");

    /* ============================================================================== */

    public static EntitySliceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntitySliceHandle handle = new EntitySliceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final EntitySliceHandle createNew(Class<?> oclass) {
        return T.constr_oclass.newInstance(oclass);
    }

    /* ============================================================================== */

    public boolean add(Object value) {
        return T.add.invoke(instance, value);
    }

    public boolean remove(Object value) {
        return T.remove.invoke(instance, value);
    }

    public List<Object> getListValues() {
        return T.listValues.get(instance);
    }

    public void setListValues(List<Object> value) {
        T.listValues.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntitySlice</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntitySliceClass extends Template.Class<EntitySliceHandle> {
        public final Template.Constructor.Converted<EntitySliceHandle> constr_oclass = new Template.Constructor.Converted<EntitySliceHandle>();

        public final Template.Field<List<Object>> listValues = new Template.Field<List<Object>>();

        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Boolean> remove = new Template.Method<Boolean>();

    }

}

