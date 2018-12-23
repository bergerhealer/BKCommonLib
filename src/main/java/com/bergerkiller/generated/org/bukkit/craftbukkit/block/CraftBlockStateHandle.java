package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftBlockStateHandle extends Template.Handle {
    /** @See {@link CraftBlockStateClass} */
    public static final CraftBlockStateClass T = new CraftBlockStateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftBlockStateHandle.class, "org.bukkit.craftbukkit.block.CraftBlockState");

    /* ============================================================================== */

    public static CraftBlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final BlockState createNew(Block block) {
        return T.constr_block.newInstance(block);
    }

    /* ============================================================================== */

    public abstract World getWorld();
    public abstract void setWorld(World value);
    public abstract Chunk getChunk();
    public abstract void setChunk(Chunk value);
    public abstract int getFlag();
    public abstract void setFlag(int value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockStateClass extends Template.Class<CraftBlockStateHandle> {
        public final Template.Constructor.Converted<BlockState> constr_block = new Template.Constructor.Converted<BlockState>();

        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Converted<Chunk> chunk = new Template.Field.Converted<Chunk>();
        public final Template.Field.Integer flag = new Template.Field.Integer();

    }

}

