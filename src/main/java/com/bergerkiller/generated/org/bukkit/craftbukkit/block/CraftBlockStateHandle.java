package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.World;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.Chunk;

public class CraftBlockStateHandle extends Template.Handle {
    public static final CraftBlockStateClass T = new CraftBlockStateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftBlockStateHandle.class, "org.bukkit.craftbukkit.block.CraftBlockState");

    /* ============================================================================== */

    public static CraftBlockStateHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftBlockStateHandle handle = new CraftBlockStateHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final BlockState createNew(Block block) {
        return T.constr_block.newInstance(block);
    }

    /* ============================================================================== */

    public World getWorld() {
        return T.world.get(instance);
    }

    public void setWorld(World value) {
        T.world.set(instance, value);
    }

    public Chunk getChunk() {
        return T.chunk.get(instance);
    }

    public void setChunk(Chunk value) {
        T.chunk.set(instance, value);
    }

    public int getX() {
        return T.x.getInteger(instance);
    }

    public void setX(int value) {
        T.x.setInteger(instance, value);
    }

    public int getY() {
        return T.y.getInteger(instance);
    }

    public void setY(int value) {
        T.y.setInteger(instance, value);
    }

    public int getZ() {
        return T.z.getInteger(instance);
    }

    public void setZ(int value) {
        T.z.setInteger(instance, value);
    }

    public int getTypeId() {
        return T.typeId.getInteger(instance);
    }

    public void setTypeId(int value) {
        T.typeId.setInteger(instance, value);
    }

    public MaterialData getData() {
        return T.data.get(instance);
    }

    public void setData(MaterialData value) {
        T.data.set(instance, value);
    }

    public int getFlag() {
        return T.flag.getInteger(instance);
    }

    public void setFlag(int value) {
        T.flag.setInteger(instance, value);
    }

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

