package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.core.BaseBlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkCoordIntPairHandle;
import com.bergerkiller.generated.net.minecraft.server.Vec3DHandle;

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
        return Vec3DHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
    }

    public static Vector getVec(Object vec3D) {
        return new Vector(Vec3DHandle.T.x.getDouble(vec3D),
                Vec3DHandle.T.y.getDouble(vec3D),
                Vec3DHandle.T.z.getDouble(vec3D));
    }

    public static double getVecX(Object vec3D) {
        return Vec3DHandle.T.x.getDouble(vec3D);
    }

    public static double getVecY(Object vec3D) {
        return Vec3DHandle.T.y.getDouble(vec3D);
    }

    public static double getVecZ(Object vec3D) {
        return Vec3DHandle.T.z.getDouble(vec3D);
    }

    public static boolean isVec(Object vec3D) {
        return Vec3DHandle.T.isAssignableFrom(vec3D);
    }

    /* ============================================================== */
    /* ===================== ChunkCoordIntPair ====================== */
    /* ============================================================== */

    public static Object newPair(int x, int z) {
        return ChunkCoordIntPairHandle.T.constr_x_z.raw.newInstance(x, z);
    }

    public static IntVector2 getPair(Object chunkCoordIntPair) {
        return new IntVector2(ChunkCoordIntPairHandle.T.x.getInteger(chunkCoordIntPair),
                ChunkCoordIntPairHandle.T.z.getInteger(chunkCoordIntPair));
    }

    public static int getPairX(Object chunkCoordIntPair) {
        return ChunkCoordIntPairHandle.T.x.getInteger(chunkCoordIntPair);
    }

    public static int getPairZ(Object chunkCoordIntPair) {
        return ChunkCoordIntPairHandle.T.z.getInteger(chunkCoordIntPair);
    }

    public static boolean isPair(Object chunkCoordIntPair) {
        return ChunkCoordIntPairHandle.T.isAssignableFrom(chunkCoordIntPair);
    }

    /* ============================================================== */
    /* ======================= BlockPosition ======================== */
    /* ============================================================== */

    public static Object newPosition(int x, int y, int z) {
        return BlockPositionHandle.createNew(x, y, z).getRaw();
    }

    public static IntVector3 getPosition(Object blockPosition) {
        return BaseBlockPositionHandle.T.toIntVector3.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionX(Object blockPosition) {
        return BaseBlockPositionHandle.T.getX.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionY(Object blockPosition) {
        return BaseBlockPositionHandle.T.getY.invoke(blockPosition);
    }

    @Deprecated
    public static int getPositionZ(Object blockPosition) {
        return BaseBlockPositionHandle.T.getZ.invoke(blockPosition);
    }

    public static boolean isPosition(Object blockPosition) {
        return BlockPositionHandle.T.isAssignableFrom(blockPosition);
    }

    public static boolean isPositionInBox(Object blockPosition, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        return BaseBlockPositionHandle.createHandle(blockPosition).isPositionInBox(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    /* ============================================================== */
    /* ======================= AxisAlignedBB ======================== */
    /* ============================================================== */

    public static Object newAxisAlignedBB(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        return AxisAlignedBBHandle.T.constr_x1_y1_z1_x2_y2_z2.raw.newInstanceVA(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public static Object growAxisAlignedBB(Object axisAlignedBB, double growX, double growY, double growZ) {
        return AxisAlignedBBHandle.T.grow.raw.invoke(axisAlignedBB, growX, growY, growZ);
    }
}
