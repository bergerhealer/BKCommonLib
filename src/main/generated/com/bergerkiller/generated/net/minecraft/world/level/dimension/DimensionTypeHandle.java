package com.bergerkiller.generated.net.minecraft.world.level.dimension;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.dimension.DimensionType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.dimension.DimensionType")
public abstract class DimensionTypeHandle extends Template.Handle {
    /** @see DimensionTypeClass */
    public static final DimensionTypeClass T = Template.Class.create(DimensionTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DimensionTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getDimensionTypeRegistry() {
        return T.getDimensionTypeRegistry.invoker.invoke(null);
    }

    public static DimensionTypeHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    public abstract boolean hasSkyLight();
    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.dimension.DimensionType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DimensionTypeClass extends Template.Class<DimensionTypeHandle> {
        public final Template.StaticMethod<Object> getDimensionTypeRegistry = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<DimensionTypeHandle> fromId = new Template.StaticMethod.Converted<DimensionTypeHandle>();

        public final Template.Method<Boolean> hasSkyLight = new Template.Method<Boolean>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

