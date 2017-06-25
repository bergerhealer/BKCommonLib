package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartCommandBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityMinecartCommandBlockHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartCommandBlockClass} */
    public static final EntityMinecartCommandBlockClass T = new EntityMinecartCommandBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartCommandBlockHandle.class, "net.minecraft.server.EntityMinecartCommandBlock");

    /* ============================================================================== */

    public static EntityMinecartCommandBlockHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityMinecartCommandBlockHandle handle = new EntityMinecartCommandBlockHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public static final Key<String> DATA_COMMAND = Key.fromTemplate(T.DATA_COMMAND, 23);
    public static final Key<Object> DATA_PREVIOUS_COMMAND = Key.fromTemplate(T.DATA_PREVIOUS_COMMAND, 24);
    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartCommandBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartCommandBlockClass extends Template.Class<EntityMinecartCommandBlockHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<String>> DATA_COMMAND = new Template.StaticField.Converted<Key<String>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_PREVIOUS_COMMAND = new Template.StaticField.Converted<Key<Object>>();

    }

}

