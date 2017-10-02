package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.CraftBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftBlockHandle extends Template.Handle {
    /** @See {@link CraftBlockClass} */
    public static final CraftBlockClass T = new CraftBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftBlockHandle.class, "org.bukkit.craftbukkit.block.CraftBlock");

    /* ============================================================================== */

    public static CraftBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Block createNew(Chunk chunk, int x, int y, int z) {
        return T.constr_chunk_x_y_z.newInstance(chunk, x, y, z);
    }

    /* ============================================================================== */

    public abstract Chunk getChunk();
    public abstract void setChunk(Chunk value);
    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getY();
    public abstract void setY(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.CraftBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockClass extends Template.Class<CraftBlockHandle> {
        public final Template.Constructor.Converted<Block> constr_chunk_x_y_z = new Template.Constructor.Converted<Block>();

        public final Template.Field.Converted<Chunk> chunk = new Template.Field.Converted<Chunk>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

    }

}

