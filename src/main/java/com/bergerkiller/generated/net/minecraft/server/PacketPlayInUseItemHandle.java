package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInUseItem</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class PacketPlayInUseItemHandle extends PacketHandle {
    /** @See {@link PacketPlayInUseItemClass} */
    public static final PacketPlayInUseItemClass T = new PacketPlayInUseItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInUseItemHandle.class, "net.minecraft.server.PacketPlayInUseItem");

    /* ============================================================================== */

    public static PacketPlayInUseItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract Object getDirection();
    public abstract void setDirection(Object value);
    public abstract Object getEnumHand();
    public abstract void setEnumHand(Object value);
    public abstract float getUnknown1();
    public abstract void setUnknown1(float value);
    public abstract float getUnknown2();
    public abstract void setUnknown2(float value);
    public abstract float getUnknown3();
    public abstract void setUnknown3(float value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInUseItem</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInUseItemClass extends Template.Class<PacketPlayInUseItemHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<Object> direction = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();
        public final Template.Field.Float unknown1 = new Template.Field.Float();
        public final Template.Field.Float unknown2 = new Template.Field.Float();
        public final Template.Field.Float unknown3 = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

    }

}

