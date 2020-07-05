package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DimensionManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class DimensionManagerHandle extends Template.Handle {
    /** @See {@link DimensionManagerClass} */
    public static final DimensionManagerClass T = new DimensionManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DimensionManagerHandle.class, "net.minecraft.server.DimensionManager", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static DimensionManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static DimensionManagerHandle fromKey(ResourceKey<DimensionType> key) {
        return T.fromKey.invoke(key);
    }

    public static DimensionManagerHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    public abstract boolean hasSkyLight();
    public abstract ResourceKey<DimensionType> getKey();
    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.server.DimensionManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DimensionManagerClass extends Template.Class<DimensionManagerHandle> {
        public final Template.StaticMethod.Converted<DimensionManagerHandle> fromKey = new Template.StaticMethod.Converted<DimensionManagerHandle>();
        public final Template.StaticMethod.Converted<DimensionManagerHandle> fromId = new Template.StaticMethod.Converted<DimensionManagerHandle>();

        public final Template.Method<Boolean> hasSkyLight = new Template.Method<Boolean>();
        public final Template.Method.Converted<ResourceKey<DimensionType>> getKey = new Template.Method.Converted<ResourceKey<DimensionType>>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

