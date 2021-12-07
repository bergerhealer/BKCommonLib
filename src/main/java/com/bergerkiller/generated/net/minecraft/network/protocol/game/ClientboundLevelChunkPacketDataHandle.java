package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData")
public abstract class ClientboundLevelChunkPacketDataHandle extends Template.Handle {
    /** @See {@link ClientboundLevelChunkPacketDataClass} */
    public static final ClientboundLevelChunkPacketDataClass T = Template.Class.create(ClientboundLevelChunkPacketDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundLevelChunkPacketDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundLevelChunkPacketDataClass extends Template.Class<ClientboundLevelChunkPacketDataHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityData</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.Optional
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityData")
    public abstract static class BlockEntityDataHandle extends Template.Handle {
        /** @See {@link BlockEntityDataClass} */
        public static final BlockEntityDataClass T = Template.Class.create(BlockEntityDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static BlockEntityDataHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static Object encodeRaw(IntVector3 position, BlockStateType type, CommonTagCompound tag) {
            return T.encodeRaw.invoke(position, type, tag);
        }

        public abstract IntVector3 getPosition(int chunkX, int chunkZ);
        public abstract BlockStateType getType();
        public abstract void setType(BlockStateType value);
        public abstract CommonTagCompound getTag();
        public abstract void setTag(CommonTagCompound value);
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityData</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class BlockEntityDataClass extends Template.Class<BlockEntityDataHandle> {
            public final Template.Field.Converted<BlockStateType> type = new Template.Field.Converted<BlockStateType>();
            public final Template.Field.Converted<CommonTagCompound> tag = new Template.Field.Converted<CommonTagCompound>();

            public final Template.StaticMethod.Converted<Object> encodeRaw = new Template.StaticMethod.Converted<Object>();

            public final Template.Method<IntVector3> getPosition = new Template.Method<IntVector3>();

        }

    }

}

