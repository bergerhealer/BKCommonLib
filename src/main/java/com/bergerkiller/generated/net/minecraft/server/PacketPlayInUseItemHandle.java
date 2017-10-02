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
public class PacketPlayInUseItemHandle extends PacketHandle {
    /** @See {@link PacketPlayInUseItemClass} */
    public static final PacketPlayInUseItemClass T = new PacketPlayInUseItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInUseItemHandle.class, "net.minecraft.server.PacketPlayInUseItem");

    /* ============================================================================== */

    public static PacketPlayInUseItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public IntVector3 getPosition() {
        return T.position.get(getRaw());
    }

    public void setPosition(IntVector3 value) {
        T.position.set(getRaw(), value);
    }

    public Object getDirection() {
        return T.direction.get(getRaw());
    }

    public void setDirection(Object value) {
        T.direction.set(getRaw(), value);
    }

    public Object getEnumHand() {
        return T.enumHand.get(getRaw());
    }

    public void setEnumHand(Object value) {
        T.enumHand.set(getRaw(), value);
    }

    public float getUnknown1() {
        return T.unknown1.getFloat(getRaw());
    }

    public void setUnknown1(float value) {
        T.unknown1.setFloat(getRaw(), value);
    }

    public float getUnknown2() {
        return T.unknown2.getFloat(getRaw());
    }

    public void setUnknown2(float value) {
        T.unknown2.setFloat(getRaw(), value);
    }

    public float getUnknown3() {
        return T.unknown3.getFloat(getRaw());
    }

    public void setUnknown3(float value) {
        T.unknown3.setFloat(getRaw(), value);
    }

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

