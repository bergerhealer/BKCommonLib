package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInUseEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInUseEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayInUseEntityClass} */
    public static final PacketPlayInUseEntityClass T = new PacketPlayInUseEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInUseEntityHandle.class, "net.minecraft.server.PacketPlayInUseEntity");

    /* ============================================================================== */

    public static PacketPlayInUseEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInUseEntityHandle handle = new PacketPlayInUseEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getUsedEntityId() {
        return T.usedEntityId.getInteger(instance);
    }

    public void setUsedEntityId(int value) {
        T.usedEntityId.setInteger(instance, value);
    }

    public UseAction getAction() {
        return T.action.get(instance);
    }

    public void setAction(UseAction value) {
        T.action.set(instance, value);
    }

    public Vector getOffset() {
        return T.offset.get(instance);
    }

    public void setOffset(Vector value) {
        T.offset.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInUseEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInUseEntityClass extends Template.Class<PacketPlayInUseEntityHandle> {
        public final Template.Field.Integer usedEntityId = new Template.Field.Integer();
        public final Template.Field.Converted<UseAction> action = new Template.Field.Converted<UseAction>();
        public final Template.Field.Converted<Vector> offset = new Template.Field.Converted<Vector>();
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class EnumEntityUseActionHandle extends Template.Handle {
        /** @See {@link EnumEntityUseActionClass} */
        public static final EnumEntityUseActionClass T = new EnumEntityUseActionClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumEntityUseActionHandle.class, "net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction");

        public static final EnumEntityUseActionHandle INTERACT = T.INTERACT.getSafe();
        public static final EnumEntityUseActionHandle ATTACK = T.ATTACK.getSafe();
        public static final EnumEntityUseActionHandle INTERACT_AT = T.INTERACT_AT.getSafe();
        /* ============================================================================== */

        public static EnumEntityUseActionHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            EnumEntityUseActionHandle handle = new EnumEntityUseActionHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumEntityUseActionClass extends Template.Class<EnumEntityUseActionHandle> {
            public final Template.EnumConstant.Converted<EnumEntityUseActionHandle> INTERACT = new Template.EnumConstant.Converted<EnumEntityUseActionHandle>();
            public final Template.EnumConstant.Converted<EnumEntityUseActionHandle> ATTACK = new Template.EnumConstant.Converted<EnumEntityUseActionHandle>();
            public final Template.EnumConstant.Converted<EnumEntityUseActionHandle> INTERACT_AT = new Template.EnumConstant.Converted<EnumEntityUseActionHandle>();

        }

    }

}

