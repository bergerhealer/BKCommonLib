package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.EntitySlice</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.util.EntitySlice")
public abstract class EntitySliceHandle extends Template.Handle {
    /** @see EntitySliceClass */
    public static final EntitySliceClass T = Template.Class.create(EntitySliceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntitySliceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final EntitySliceHandle createNew(Class<?> oclass) {
        return T.constr_oclass.newInstance(oclass);
    }

    /* ============================================================================== */

    public abstract boolean add(Object value);
    public abstract boolean remove(Object value);
    public abstract Iterator iterator();
    public abstract int size();
    /**
     * Stores class members for <b>net.minecraft.util.EntitySlice</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntitySliceClass extends Template.Class<EntitySliceHandle> {
        public final Template.Constructor.Converted<EntitySliceHandle> constr_oclass = new Template.Constructor.Converted<EntitySliceHandle>();

        @Template.Optional
        public final Template.Field<List<Object>> listValues_1_8_3 = new Template.Field<List<Object>>();

        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Boolean> remove = new Template.Method<Boolean>();
        public final Template.Method<Iterator> iterator = new Template.Method<Iterator>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();

    }

}

