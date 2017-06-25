package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutMapChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutMapChunkHandle extends PacketHandle {
    /** @See {@link PacketPlayOutMapChunkClass} */
    public static final PacketPlayOutMapChunkClass T = new PacketPlayOutMapChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutMapChunkHandle.class, "net.minecraft.server.PacketPlayOutMapChunk");

    /* ============================================================================== */

    public static PacketPlayOutMapChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutMapChunkHandle handle = new PacketPlayOutMapChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public int getSectionsMask() {
        if (T.sectionsMask.isAvailable()) {
            return T.sectionsMask.get(instance);
        } else {
            return T.chunkInfo.get(instance).getSectionsMask();
        }
    }

    public void setSectionsMask(int sectionsMask) {
        if (T.sectionsMask.isAvailable()) {
            T.sectionsMask.set(instance, sectionsMask);
        } else {
            T.chunkInfo.get(instance).setSectionsMask(sectionsMask);
        }
    }

    public byte[] getData() {
        if (T.data.isAvailable()) {
            return T.data.get(instance);
        } else {
            return T.chunkInfo.get(instance).getData();
        }
    }

    public void setData(byte[] data) {
        if (T.data.isAvailable()) {
            T.data.set(instance, data);
        } else {
            T.chunkInfo.get(instance).setData(data);
        }
    }

    public List<CommonTagCompound> getTags() {
        if (T.tags.isAvailable()) {
            return T.tags.get(instance);
        } else {
            return java.util.Collections.emptyList();
        }
    }

    public void setTags(List<CommonTagCompound> tags) {
        if (T.tags.isAvailable()) {
            T.tags.set(instance, tags);
        } else {
        }
    }


    public static PacketPlayOutMapChunkHandle createNew(org.bukkit.Chunk chunk, int sectionsMask) {
        if (T.constr_chunk_sectionsMask.isAvailable()) {
            return T.constr_chunk_sectionsMask.newInstance(chunk, sectionsMask);
        } else {
            boolean flag = WorldHandle.fromBukkit(chunk.getWorld()).getWorldProvider().isDarkWorld();
            return T.constr_chunk_flag_sectionsMask.newInstance(chunk, flag, sectionsMask);
        }
    }
    public int getX() {
        return T.x.getInteger(instance);
    }

    public void setX(int value) {
        T.x.setInteger(instance, value);
    }

    public int getZ() {
        return T.z.getInteger(instance);
    }

    public void setZ(int value) {
        T.z.setInteger(instance, value);
    }

    public boolean isHasBiomeData() {
        return T.hasBiomeData.getBoolean(instance);
    }

    public void setHasBiomeData(boolean value) {
        T.hasBiomeData.setBoolean(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutMapChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapChunkClass extends Template.Class<PacketPlayOutMapChunkHandle> {
        @Template.Optional
        public final Template.Constructor.Converted<PacketPlayOutMapChunkHandle> constr_chunk_flag_sectionsMask = new Template.Constructor.Converted<PacketPlayOutMapChunkHandle>();
        @Template.Optional
        public final Template.Constructor.Converted<PacketPlayOutMapChunkHandle> constr_chunk_sectionsMask = new Template.Constructor.Converted<PacketPlayOutMapChunkHandle>();

        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer sectionsMask = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<byte[]> data = new Template.Field<byte[]>();
        @Template.Optional
        public final Template.Field.Converted<List<CommonTagCompound>> tags = new Template.Field.Converted<List<CommonTagCompound>>();
        @Template.Optional
        public final Template.Field.Converted<ChunkMapHandle> chunkInfo = new Template.Field.Converted<ChunkMapHandle>();
        public final Template.Field.Boolean hasBiomeData = new Template.Field.Boolean();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutMapChunk.ChunkMap</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.Optional
    public static class ChunkMapHandle extends Template.Handle {
        /** @See {@link ChunkMapClass} */
        public static final ChunkMapClass T = new ChunkMapClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkMapHandle.class, "net.minecraft.server.PacketPlayOutMapChunk.ChunkMap");

        /* ============================================================================== */

        public static ChunkMapHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            ChunkMapHandle handle = new ChunkMapHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        public byte[] getData() {
            return T.data.get(instance);
        }

        public void setData(byte[] value) {
            T.data.set(instance, value);
        }

        public int getSectionsMask() {
            return T.sectionsMask.getInteger(instance);
        }

        public void setSectionsMask(int value) {
            T.sectionsMask.setInteger(instance, value);
        }

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutMapChunk.ChunkMap</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ChunkMapClass extends Template.Class<ChunkMapHandle> {
            public final Template.Field<byte[]> data = new Template.Field<byte[]>();
            public final Template.Field.Integer sectionsMask = new Template.Field.Integer();

        }

    }

}

