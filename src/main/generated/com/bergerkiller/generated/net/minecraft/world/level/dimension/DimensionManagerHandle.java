package com.bergerkiller.generated.net.minecraft.world.level.dimension;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.dimension.DimensionManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.dimension.DimensionManager")
public abstract class DimensionManagerHandle extends Template.Handle {
    /** @see DimensionManagerClass */
    public static final DimensionManagerClass T = Template.Class.create(DimensionManagerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DimensionManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getDimensionTypeRegistry() {
        return T.getDimensionTypeRegistry.invoker.invoke(null);
    }

    public static DimensionManagerHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    public abstract boolean hasSkyLight();
    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.dimension.DimensionManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DimensionManagerClass extends Template.Class<DimensionManagerHandle> {
        public final Template.StaticMethod<Object> getDimensionTypeRegistry = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<DimensionManagerHandle> fromId = new Template.StaticMethod.Converted<DimensionManagerHandle>();

        public final Template.Method<Boolean> hasSkyLight = new Template.Method<Boolean>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

