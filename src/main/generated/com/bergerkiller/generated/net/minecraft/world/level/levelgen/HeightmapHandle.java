package com.bergerkiller.generated.net.minecraft.world.level.levelgen;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.levelgen.Heightmap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.levelgen.Heightmap")
public abstract class HeightmapHandle extends Template.Handle {
    /** @see HeightmapClass */
    public static final HeightmapClass T = Template.Class.create(HeightmapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static HeightmapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract LevelChunkHandle getChunk();
    public abstract int getHeight(int x, int z);
    public abstract void setHeight(int x, int z, int height);
    public abstract void initialize();
    /**
     * Stores class members for <b>net.minecraft.world.level.levelgen.Heightmap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HeightmapClass extends Template.Class<HeightmapHandle> {
        public final Template.Method.Converted<LevelChunkHandle> getChunk = new Template.Method.Converted<LevelChunkHandle>();
        public final Template.Method<Integer> getHeight = new Template.Method<Integer>();
        public final Template.Method<Void> setHeight = new Template.Method<Void>();
        public final Template.Method<Void> initialize = new Template.Method<Void>();

    }

}

