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

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace value);
    public abstract ActionHandle getDigType();
    public abstract void setDigType(ActionHandle value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPlayerActionPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundPlayerActionPacketClass extends Template.Class<ServerboundPlayerActionPacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockFace> direction = new Template.Field.Converted<BlockFace>();
        public final Template.Field.Converted<ActionHandle> digType = new Template.Field.Converted<ActionHandle>();

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

