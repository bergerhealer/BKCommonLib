package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.WorldDataHandle;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.generated.net.minecraft.server.MethodProfilerHandle;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import org.bukkit.World;
import java.util.List;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class WorldHandle extends Template.Handle {
    public static final WorldClass T = new WorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldHandle.class, "net.minecraft.server.World");


    /* ============================================================================== */

    public static final WorldHandle createHandle(Object handleInstance) {
        if (handleInstance == null) throw new IllegalArgumentException("Handle instance can not be null");
        WorldHandle handle = new WorldHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public World getWorld() {
        return T.getWorld.invoke(instance);
    }

    public BlockData getBlockData(IntVector3 blockposition) {
        return T.getBlockData.invoke(instance, blockposition);
    }

    public long getTime() {
        return T.getTime.invoke(instance);
    }

    public boolean getBlockCollisions(EntityHandle entity, AxisAlignedBBHandle bounds, boolean flag, List<AxisAlignedBBHandle> list) {
        return T.getBlockCollisions.invoke(instance, entity, bounds, flag, list);
    }

    public List<AxisAlignedBBHandle> getCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getCubes.invoke(instance, entity, axisalignedbb);
    }

    public List<EntityHandle> getEntities(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getEntities.invoke(instance, entity, axisalignedbb);
    }

    public WorldDataHandle getWorldData() {
        return T.getWorldData.invoke(instance);
    }

    public boolean isBurnArea(AxisAlignedBBHandle bounds) {
        return T.isBurnArea.invoke(instance, bounds);
    }

    public MethodProfilerHandle getMethodProfiler() {
        return T.methodProfiler.get(instance);
    }

    public void setMethodProfiler(MethodProfilerHandle value) {
        T.methodProfiler.set(instance, value);
    }

    public static final class WorldClass extends Template.Class {
        public final Template.Field.Converted<MethodProfilerHandle> methodProfiler = new Template.Field.Converted<MethodProfilerHandle>();

        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted<World>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method<Long> getTime = new Template.Method<Long>();
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> getCubes = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<EntityHandle>> getEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<WorldDataHandle> getWorldData = new Template.Method.Converted<WorldDataHandle>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();

    }
}
