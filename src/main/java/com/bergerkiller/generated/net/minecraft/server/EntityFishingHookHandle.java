package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityFishingHook</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityFishingHookHandle extends EntityHandle {
    /** @See {@link EntityFishingHookClass} */
    public static final EntityFishingHookClass T = new EntityFishingHookClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityFishingHookHandle.class, "net.minecraft.server.EntityFishingHook", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityFishingHookHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<java.util.OptionalInt> DATA_HOOKED_ENTITY_ID = Key.Type.ENTITY_ID.createKey(T.DATA_HOOKED_ENTITY_ID, -1);
    /**
     * Stores class members for <b>net.minecraft.server.EntityFishingHook</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityFishingHookClass extends Template.Class<EntityFishingHookHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_HOOKED_ENTITY_ID = new Template.StaticField.Converted<Key<Integer>>();

    }

}

