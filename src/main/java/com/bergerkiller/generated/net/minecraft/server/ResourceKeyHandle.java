package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ResourceKey</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ResourceKeyHandle extends Template.Handle {
    /** @See {@link ResourceKeyClass} */
    public static final ResourceKeyClass T = new ResourceKeyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ResourceKeyHandle.class, "net.minecraft.server.ResourceKey", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static ResourceKeyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ResourceKeyHandle create(ResourceKeyHandle category, MinecraftKeyHandle name) {
        return T.create.invoke(category, name);
    }

    public static ResourceKeyHandle createCategory(MinecraftKeyHandle categoryName) {
        return T.createCategory.invoke(categoryName);
    }

    public abstract MinecraftKeyHandle getCategory();
    public abstract MinecraftKeyHandle getName();
    /**
     * Stores class members for <b>net.minecraft.server.ResourceKey</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ResourceKeyClass extends Template.Class<ResourceKeyHandle> {
        public final Template.StaticMethod.Converted<ResourceKeyHandle> create = new Template.StaticMethod.Converted<ResourceKeyHandle>();
        public final Template.StaticMethod.Converted<ResourceKeyHandle> createCategory = new Template.StaticMethod.Converted<ResourceKeyHandle>();

        public final Template.Method.Converted<MinecraftKeyHandle> getCategory = new Template.Method.Converted<MinecraftKeyHandle>();
        public final Template.Method.Converted<MinecraftKeyHandle> getName = new Template.Method.Converted<MinecraftKeyHandle>();

    }

}

