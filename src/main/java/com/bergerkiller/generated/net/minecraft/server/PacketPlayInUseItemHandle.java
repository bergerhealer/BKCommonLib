package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInUseItem</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayInUseItemHandle extends PacketHandle {
    /** @See {@link PacketPlayInUseItemClass} */
    public static final PacketPlayInUseItemClass T = new PacketPlayInUseItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInUseItemHandle.class, "net.minecraft.server.PacketPlayInUseItem");

    /* ============================================================================== */

    public static PacketPlayInUseItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace direction);

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_USE_ITEM;
    }

    public void setTimestamp(long timestamp) {
        if (T.timestamp.isAvailable()) {
            T.timestamp.setLong(getRaw(), timestamp);
        }
    }

    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return internalGetHand(T.opt_enumHand, humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        internalSetHand(T.opt_enumHand, humanEntity, hand);
    }
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract float getDeltaX();
    public abstract void setDeltaX(float value);
    public abstract float getDeltaY();
    public abstract void setDeltaY(float value);
    public abstract float getDeltaZ();
    public abstract void setDeltaZ(float value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInUseItem</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInUseItemClass extends Template.Class<PacketPlayInUseItemHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        @Template.Optional
        public final Template.Field.Converted<Object> opt_direction_face = new Template.Field.Converted<Object>();
        @Template.Optional
        public final Template.Field.Integer opt_direction_index = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<Object> opt_enumHand = new Template.Field.Converted<Object>();
        public final Template.Field.Float deltaX = new Template.Field.Float();
        public final Template.Field.Float deltaY = new Template.Field.Float();
        public final Template.Field.Float deltaZ = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted<BlockFace>();
        public final Template.Method.Converted<Void> setDirection = new Template.Method.Converted<Void>();

    }

}

