package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftBlockStateHandle extends Template.Handle {
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

    public World getWorld() {
        return T.world.get(getRaw());
    }

    public void setWorld(World value) {
        T.world.set(getRaw(), value);
    }

    public Chunk getChunk() {
        return T.chunk.get(getRaw());
    }

    public void setChunk(Chunk value) {
        T.chunk.set(getRaw(), value);
    }

    public int getX() {
        return T.x.getInteger(getRaw());
    }

    public void setX(int value) {
        T.x.setInteger(getRaw(), value);
    }

    public int getY() {
        return T.y.getInteger(getRaw());
    }

    public void setY(int value) {
        T.y.setInteger(getRaw(), value);
    }

    public int getZ() {
        return T.z.getInteger(getRaw());
    }

    public void setZ(int value) {
        T.z.setInteger(getRaw(), value);
    }

    public int getTypeId() {
        return T.typeId.getInteger(getRaw());
    }

    public void setTypeId(int value) {
        T.typeId.setInteger(getRaw(), value);
    }

    public MaterialData getData() {
        return T.data.get(getRaw());
    }

    public void setData(MaterialData value) {
        T.data.set(getRaw(), value);
    }

    public int getFlag() {
        return T.flag.getInteger(getRaw());
    }

    public void setFlag(int value) {
        T.flag.setInteger(getRaw(), value);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockStateClass extends Template.Class<CraftBlockStateHandle> {
        public final Template.Constructor.Converted<BlockState> constr_block = new Template.Constructor.Converted<BlockState>();

        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Converted<Chunk> chunk = new Template.Field.Converted<Chunk>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Integer typeId = new Template.Field.Integer();
        public final Template.Field<MaterialData> data = new Template.Field<MaterialData>();
        public final Template.Field.Integer flag = new Template.Field.Integer();

    }

}

