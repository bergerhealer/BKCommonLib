package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.core.Vec3iHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ChunkPosHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3Handle;

import org.bukkit.util.Vector;

/**
 * All NMS Vector related classes can be used here. Vec3D,
 * ChunkCoordIntPair and BlockPosition are supported. No reflection is used
 * to minimize overhead.<br>
 * <br>
 * <b>Deprecated: these methods suffer performance problems, use the Handle declared conversion
 * methods instead</b>
 */
@Deprecated
public class NMSVector {

    /* ============================================================== */
    /* =========================== Vec3D ============================ */
    /* ============================================================== */

    public static Object newVec(double x, double y, double z) {
        return Vec3Handle.T.constr_x_y_z.raw.newInstance(x, y, z);
    }

    public static Vector getVec(Object vec3D) {
        return new Vector(Vec3Handle.T.x.getDouble(vec3D),
                Vec3Handle.T.y.getDouble(vec3D),
                Vec3Handle.T.z.getDouble(vec3D));
    }

    public static double getVecX(Object vec3D) {
        return Vec3Handle.T.x.getDouble(vec3D);
    }

    public static double getVecY(Object vec3D) {
        return Vec3Handle.T.y.getDouble(vec3D);
    }

    public static double getVecZ(Object vec3D) {
        return Vec3Handle.T.z.getDouble(vec3D);
    }

    public static boolean isVec(Object vec3D) {
        return Vec3Handle.T.isAssignableFrom(vec3D);
    }

    /* ============================================================== */
    /* ===================== ChunkCoordIntPair ====================== */
    /* ============================================================== */

    public static Object newPair(int x, int z) {
        return ChunkPosHandle.T.constr_x_z.raw.newInstance(x, z);
    }

    public static IntVector2 getPair(Object chunkCoordIntPair) {
        return ChunkPosHandle.T.toIntVector2.invoke(chunkCoordIntPair);
    }

    public static int getPairX(Object chunkCoordIntPair) {
        return ChunkPosHandle.T.x.invoke(chunkCoordIntPair);
    }

    public static int getPairZ(Object chunkCoordIntPair) {
        return ChunkPosHandle.T.z.invoke(chunkCoordIntPair);
    }

    public static boolean isPair(Object chunkCoordIntPair) {
        return ChunkPosHandle.T.isAssignableFrom(chunkCoordIntPair);
    }

    /* ============================================================== */
    /* ======================= BlockPosition ======================== */
    /* ============================================================== */

    public static Object newPosition(int x, int y, int z) {
        return BlockPosHandle.createNew(x, y, z).getRaw();
    }

    public static IntVector3 getPosition(Object blockPosition) {
        return Vec3iHandle.T.toIntVector3.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionX(Object blockPosition) {
        return Vec3iHandle.T.getX.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionY(Object blockPosition) {
        return Vec3iHandle.T.getY.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionZ(Object blockPosition) {
        return Vec3iHandle.T.getZ.invoke(blockPosition);
    }

    public static boolean isPosition(Object blockPosition) {
        return BlockPosHandle.T.isAssignableFrom(blockPosition);
    }

    public static boolean isPositionInBox(Object blockPosition, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        return Vec3iHandle.createHandle(blockPosition).isPositionInBox(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    /* ============================================================== */
    /* ======================= AxisAlignedBB ======================== */
    /* ============================================================== */

    public static Object newAxisAlignedBB(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        return AABBHandle.T.constr_x1_y1_z1_x2_y2_z2.raw.newInstanceVA(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public static Object growAxisAlignedBB(Object axisAlignedBB, double growX, double growY, double growZ) {
        return AABBHandle.T.grow.raw.invoke(axisAlignedBB, growX, growY, growZ);
    }
}
