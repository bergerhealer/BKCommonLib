package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundPlayerActionPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundPlayerActionPacket")
public abstract class ServerboundPlayerActionPacketHandle extends PacketHandle {
    /** @see ServerboundPlayerActionPacketClass */
    public static final ServerboundPlayerActionPacketClass T = Template.Class.create(ServerboundPlayerActionPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundPlayerActionPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundPlayerActionPacketHandle createNew(IntVector3 position, BlockFace direction, ActionHandle action, int sequence) {
        return T.createNew.invoke(position, direction, action, sequence);
    }

    public abstract int getSequence();
    public abstract void setSequence(int sequence);
    /** @deprecated Use {@link #getAction()} instead */
    @Deprecated
    public ActionHandle getDigType() { return getAction(); }

    /** @deprecated Use {@link #setAction(ActionHandle)} instead */
    @Deprecated
    public void setDigType(ActionHandle action) { setAction(action); }
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace value);
    public abstract ActionHandle getAction();
    public abstract void setAction(ActionHandle value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPlayerActionPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundPlayerActionPacketClass extends Template.Class<ServerboundPlayerActionPacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockFace> direction = new Template.Field.Converted<BlockFace>();
        public final Template.Field.Converted<ActionHandle> action = new Template.Field.Converted<ActionHandle>();

        public final Template.StaticMethod.Converted<ServerboundPlayerActionPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundPlayerActionPacketHandle>();

        public final Template.Method<Integer> getSequence = new Template.Method<Integer>();
        public final Template.Method<Void> setSequence = new Template.Method<Void>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action")
    public abstract static class ActionHandle extends Template.Handle {
        /** @see ActionClass */
        public static final ActionClass T = Template.Class.create(ActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final ActionHandle START_DESTROY_BLOCK = T.START_DESTROY_BLOCK.getSafe();
        public static final ActionHandle ABORT_DESTROY_BLOCK = T.ABORT_DESTROY_BLOCK.getSafe();
        public static final ActionHandle STOP_DESTROY_BLOCK = T.STOP_DESTROY_BLOCK.getSafe();
        public static final ActionHandle DROP_ALL_ITEMS = T.DROP_ALL_ITEMS.getSafe();
        public static final ActionHandle DROP_ITEM = T.DROP_ITEM.getSafe();
        public static final ActionHandle RELEASE_USE_ITEM = T.RELEASE_USE_ITEM.getSafe();
        /* ============================================================================== */

        public static ActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ActionClass extends Template.Class<ActionHandle> {
            public final Template.EnumConstant.Converted<ActionHandle> START_DESTROY_BLOCK = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> ABORT_DESTROY_BLOCK = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> STOP_DESTROY_BLOCK = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> DROP_ALL_ITEMS = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> DROP_ITEM = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> RELEASE_USE_ITEM = new Template.EnumConstant.Converted<ActionHandle>();

        }

    }

}

