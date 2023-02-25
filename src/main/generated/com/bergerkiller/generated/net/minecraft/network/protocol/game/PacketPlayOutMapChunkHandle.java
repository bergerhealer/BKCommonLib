package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutMapChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutMapChunk")
public abstract class PacketPlayOutMapChunkHandle extends PacketHandle {
    /** @see PacketPlayOutMapChunkClass */
    public static final PacketPlayOutMapChunkClass T = Template.Class.create(PacketPlayOutMapChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutMapChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutMapChunkHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract CommonTagCompound getHeightmaps();
    public abstract void setHeightmaps(CommonTagCompound heightmapsData);
    public abstract byte[] getBuffer();
    public abstract void setBuffer(byte[] buffer);
    public abstract List<BlockStateChange> getBlockStates();

    public void setBlockStates(List<BlockStateChange> states) {
        List<BlockStateChange> baseStates = this.getBlockStates();
        int count = states.size();
        int limit = Math.min(count, baseStates.size());
        for (int i = 0; i < limit; i++) {
            BlockStateChange change = states.get(i);
            if (baseStates.get(i) != change) {
                baseStates.set(i, change);
            }
        }
        for (int i = limit; i < count; i++) {
            baseStates.add(states.get(i));
        }
        while (baseStates.size() > count) {
            baseStates.remove(baseStates.size()-1);
        }
    }
    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutMapChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapChunkClass extends Template.Class<PacketPlayOutMapChunkHandle> {
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutMapChunkHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutMapChunkHandle>();

        public final Template.Method.Converted<CommonTagCompound> getHeightmaps = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<Void> setHeightmaps = new Template.Method.Converted<Void>();
        public final Template.Method<byte[]> getBuffer = new Template.Method<byte[]>();
        public final Template.Method<Void> setBuffer = new Template.Method<Void>();
        public final Template.Method<List<BlockStateChange>> getBlockStates = new Template.Method<List<BlockStateChange>>();

    }

}

