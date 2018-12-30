package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityBat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityBatHandle extends EntityInsentientHandle {
    /** @See {@link EntityBatClass} */
    public static final EntityBatClass T = new EntityBatClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityBatHandle.class, "net.minecraft.server.EntityBat", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityBatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<Byte> DATA_BAT_FLAGS = Key.Type.BYTE.createKey(T.DATA_BAT_FLAGS, 16);
    public static final int DATA_BAT_FLAG_HANGING = (1 << 0);
    /**
     * Stores class members for <b>net.minecraft.server.EntityBat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityBatClass extends Template.Class<EntityBatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_BAT_FLAGS = new Template.StaticField.Converted<Key<Byte>>();

    }

}

