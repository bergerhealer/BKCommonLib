package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutTileEntityData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutTileEntityData")
public abstract class PacketPlayOutTileEntityDataHandle extends PacketHandle {
    /** @See {@link PacketPlayOutTileEntityDataClass} */
    public static final PacketPlayOutTileEntityDataClass T = Template.Class.create(PacketPlayOutTileEntityDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutTileEntityDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutTileEntityDataHandle createNew(IntVector3 blockPosition, BlockStateType type, CommonTagCompound data) {
        return T.constr_blockPosition_type_data.newInstance(blockPosition, type, data);
    }

    /* ============================================================================== */

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockStateType getType();
    public abstract void setType(BlockStateType value);
    public abstract CommonTagCompound getData();
    public abstract void setData(CommonTagCompound value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutTileEntityData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutTileEntityDataClass extends Template.Class<PacketPlayOutTileEntityDataHandle> {
        public final Template.Constructor.Converted<PacketPlayOutTileEntityDataHandle> constr_blockPosition_type_data = new Template.Constructor.Converted<PacketPlayOutTileEntityDataHandle>();

        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockStateType> type = new Template.Field.Converted<BlockStateType>();
        public final Template.Field.Converted<CommonTagCompound> data = new Template.Field.Converted<CommonTagCompound>();

    }

}

