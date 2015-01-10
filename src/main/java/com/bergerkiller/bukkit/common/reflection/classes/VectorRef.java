package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.v1_8_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R1.Vec3D;

/**
 * All NMS Vector related classes can be used here. ChunkCoordIntPair,
 * ChunkCoordinates, ChunkPosition and Vec3D are supported.
 */
public class VectorRef {

    public static Object newVec(double x, double y, double z) {
        return CommonNMS.newVec3D(x, y, z);
    }

    public static Vector getVec(Object vec3D) {
        Vec3D vec = (Vec3D) vec3D;
        return new Vector(vec.a, vec.b, vec.c);
    }

    public static double getVecX(Object vec3D) {
        return ((Vec3D) vec3D).a;
    }

    public static double getVecY(Object vec3D) {
        return ((Vec3D) vec3D).b;
    }

    public static double getVecZ(Object vec3D) {
        return ((Vec3D) vec3D).c;
    }

    public static boolean isVec(Object vec3D) {
        return vec3D instanceof Vec3D;
    }

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
}
