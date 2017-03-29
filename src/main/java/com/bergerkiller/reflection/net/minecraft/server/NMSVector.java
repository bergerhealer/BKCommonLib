package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;

import net.minecraft.server.v1_11_R1.AxisAlignedBB;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_11_R1.Vec3D;
import org.bukkit.util.Vector;

/**
 * All NMS Vector related classes can be used here. Vec3D,
 * ChunkCoordIntPair and BlockPosition are supported. No reflection is used
 * to minimize overhead.
 */
public class NMSVector {

    /* ============================================================== */
    /* =========================== Vec3D ============================ */
    /* ============================================================== */
	
    public static Object newVec(double x, double y, double z) {
        return new Vec3D(x, y, z);
    }

    public static Vector getVec(Object vec3D) {
        Vec3D vec = (Vec3D) vec3D;
        return new Vector(vec.x, vec.y, vec.z);
    }

    public static double getVecX(Object vec3D) {
        return ((Vec3D) vec3D).x;
    }

    public static double getVecY(Object vec3D) {
        return ((Vec3D) vec3D).y;
    }

    public static double getVecZ(Object vec3D) {
        return ((Vec3D) vec3D).z;
    }

    public static boolean isVec(Object vec3D) {
        return vec3D instanceof Vec3D;
    }

    /* ============================================================== */
    /* ===================== ChunkCoordIntPair ====================== */
    /* ============================================================== */
    
    public static Object newPair(int x, int z) {
        return new ChunkCoordIntPair(x, z);
    }

    public static IntVector2 getPair(Object chunkCoordIntPair) {
        ChunkCoordIntPair pair = (ChunkCoordIntPair) chunkCoordIntPair;
        return new IntVector2(pair.x, pair.z);
    }

    public static int getPairX(Object chunkCoordIntPair) {
        return ((ChunkCoordIntPair) chunkCoordIntPair).x;
    }

    public static int getPairZ(Object chunkCoordIntPair) {
        return ((ChunkCoordIntPair) chunkCoordIntPair).z;
    }

    public static boolean isPair(Object chunkCoordIntPair) {
        return chunkCoordIntPair instanceof ChunkCoordIntPair;
    }

    /* ============================================================== */
    /* ======================= BlockPosition ======================== */
    /* ============================================================== */

    public static Object newPosition(int x, int y, int z) {
        return new BlockPosition(x, y, z);
    }

    public static IntVector3 getPosition(Object blockPosition) {
    	BlockPosition position = (BlockPosition) blockPosition;
        return new IntVector3(position.getX(), position.getY(), position.getZ());
    }

    public static int getPositionX(Object blockPosition) {
        return ((BlockPosition) blockPosition).getX();
    }

    public static int getPositionY(Object blockPosition) {
        return ((BlockPosition) blockPosition).getY();
    }

    public static int getPositionZ(Object blockPosition) {
        return ((BlockPosition) blockPosition).getZ();
    }

    public static boolean isPosition(Object blockPosition) {
        return blockPosition instanceof BlockPosition;
    }
    
    public static boolean isPositionInBox(Object blockPosition, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
    	BlockPosition position = (BlockPosition) blockPosition;
    	int x = position.getX();
    	int y = position.getY();
    	int z = position.getZ();
    	return x >= xMin && y >= yMin && z >= zMin && x <= xMax && y <= yMax && z <= zMax;
    }

    /* ============================================================== */
    /* ======================= AxisAlignedBB ======================== */
    /* ============================================================== */

    public static Object newAxisAlignedBB(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        return new AxisAlignedBB(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public static Object growAxisAlignedBB(Object axisAlignedBB, double growX, double growY, double growZ) {
        return ((AxisAlignedBB) axisAlignedBB).grow(growX, growY, growZ);
    }
}
