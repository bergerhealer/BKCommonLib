package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInArmAnimation</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInArmAnimationHandle extends Template.Handle {
    /** @See {@link PacketPlayInArmAnimationClass} */
    public static final PacketPlayInArmAnimationClass T = new PacketPlayInArmAnimationClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInArmAnimationHandle.class, "net.minecraft.server.PacketPlayInArmAnimation");

    /* ============================================================================== */

    public static PacketPlayInArmAnimationHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInArmAnimationHandle handle = new PacketPlayInArmAnimationHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInArmAnimation</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInArmAnimationClass extends Template.Class<PacketPlayInArmAnimationHandle> {
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();

    }

}

