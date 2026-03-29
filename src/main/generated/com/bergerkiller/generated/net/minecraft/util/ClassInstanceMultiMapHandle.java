package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.ClassInstanceMultiMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.util.ClassInstanceMultiMap")
public abstract class ClassInstanceMultiMapHandle extends Template.Handle {
    /** @see ClassInstanceMultiMapClass */
    public static final ClassInstanceMultiMapClass T = Template.Class.create(ClassInstanceMultiMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClassInstanceMultiMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ClassInstanceMultiMapHandle createNew(Class<?> oclass) {
        return T.constr_oclass.newInstance(oclass);
    }

    /* ============================================================================== */

    public abstract boolean add(Object value);
    public abstract boolean remove(Object value);
    public abstract Iterator iterator();
    public abstract int size();
    /**
     * Stores class members for <b>net.minecraft.util.ClassInstanceMultiMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClassInstanceMultiMapClass extends Template.Class<ClassInstanceMultiMapHandle> {
        public final Template.Constructor.Converted<ClassInstanceMultiMapHandle> constr_oclass = new Template.Constructor.Converted<ClassInstanceMultiMapHandle>();

        @Template.Optional
        public final Template.Field<List<Object>> listValues_1_8_3 = new Template.Field<List<Object>>();

        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Boolean> remove = new Template.Method<Boolean>();
        public final Template.Method<Iterator> iterator = new Template.Method<Iterator>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();

    }

}

