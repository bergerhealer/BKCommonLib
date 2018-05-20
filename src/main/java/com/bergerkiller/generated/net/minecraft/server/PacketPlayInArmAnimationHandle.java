package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInArmAnimation</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayInArmAnimationHandle extends PacketHandle {
    /** @See {@link PacketPlayInArmAnimationClass} */
    public static final PacketPlayInArmAnimationClass T = new PacketPlayInArmAnimationClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInArmAnimationHandle.class, "net.minecraft.server.PacketPlayInArmAnimation");

    /* ============================================================================== */

    public static PacketPlayInArmAnimationHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return internalGetHand(T.enumHand, humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        internalSetHand(T.enumHand, humanEntity, hand);
    }
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInArmAnimation</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInArmAnimationClass extends Template.Class<PacketPlayInArmAnimationHandle> {
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();

    }

}

