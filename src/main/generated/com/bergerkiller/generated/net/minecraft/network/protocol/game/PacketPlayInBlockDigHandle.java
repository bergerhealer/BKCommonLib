package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInBlockDig</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInBlockDig")
public abstract class PacketPlayInBlockDigHandle extends PacketHandle {
    /** @see PacketPlayInBlockDigClass */
    public static final PacketPlayInBlockDigClass T = Template.Class.create(PacketPlayInBlockDigClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInBlockDigHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace value);
    public abstract EnumPlayerDigTypeHandle getDigType();
    public abstract void setDigType(EnumPlayerDigTypeHandle value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInBlockDig</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInBlockDigClass extends Template.Class<PacketPlayInBlockDigHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockFace> direction = new Template.Field.Converted<BlockFace>();
        public final Template.Field.Converted<EnumPlayerDigTypeHandle> digType = new Template.Field.Converted<EnumPlayerDigTypeHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInBlockDig.EnumPlayerDigType</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInBlockDig.EnumPlayerDigType")
    public abstract static class EnumPlayerDigTypeHandle extends Template.Handle {
        /** @see EnumPlayerDigTypeClass */
        public static final EnumPlayerDigTypeClass T = Template.Class.create(EnumPlayerDigTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumPlayerDigTypeHandle START_DESTROY_BLOCK = T.START_DESTROY_BLOCK.getSafe();
        public static final EnumPlayerDigTypeHandle ABORT_DESTROY_BLOCK = T.ABORT_DESTROY_BLOCK.getSafe();
        public static final EnumPlayerDigTypeHandle STOP_DESTROY_BLOCK = T.STOP_DESTROY_BLOCK.getSafe();
        public static final EnumPlayerDigTypeHandle DROP_ALL_ITEMS = T.DROP_ALL_ITEMS.getSafe();
        public static final EnumPlayerDigTypeHandle DROP_ITEM = T.DROP_ITEM.getSafe();
        public static final EnumPlayerDigTypeHandle RELEASE_USE_ITEM = T.RELEASE_USE_ITEM.getSafe();
        /* ============================================================================== */

        public static EnumPlayerDigTypeHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInBlockDig.EnumPlayerDigType</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumPlayerDigTypeClass extends Template.Class<EnumPlayerDigTypeHandle> {
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> START_DESTROY_BLOCK = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> ABORT_DESTROY_BLOCK = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> STOP_DESTROY_BLOCK = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> DROP_ALL_ITEMS = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> DROP_ITEM = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerDigTypeHandle> RELEASE_USE_ITEM = new Template.EnumConstant.Converted<EnumPlayerDigTypeHandle>();

        }

    }

}

