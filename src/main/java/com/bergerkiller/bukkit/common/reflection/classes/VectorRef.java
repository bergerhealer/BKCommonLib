package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Vec3D;

/**
 * All NMS Vector related classes can be used here.
 * ChunkCoordIntPair, ChunkCoordinates, ChunkPosition and Vec3D are supported.
 */
public class VectorRef {

	public static Object newVec(double x, double y, double z) {
		return CommonNMS.newVec3D(x, y, z);
	}

	public static Vector getVec(Object vec3D) {
		Vec3D vec = (Vec3D) vec3D;
		return new Vector(vec.c, vec.d, vec.e);
	}

	public static double getVecX(Object vec3D) {
		return ((Vec3D) vec3D).c;
	}

	public static double getVecY(Object vec3D) {
		return ((Vec3D) vec3D).d;
	}

	public static double getVecZ(Object vec3D) {
		return ((Vec3D) vec3D).e;
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

	public static Object newCoord(int x, int y, int z) {
		return new ChunkCoordinates(x, y, z);
	}

	public static IntVector3 getCoord(Object chunkCoordinates) {
		ChunkCoordinates coord = (ChunkCoordinates) chunkCoordinates;
		return new IntVector3(coord.x, coord.y, coord.z);
	}

	public static int getCoordX(Object chunkCoordinates) {
		return ((ChunkCoordinates) chunkCoordinates).x;
	}

	public static int getCoordY(Object chunkCoordinates) {
		return ((ChunkCoordinates) chunkCoordinates).y;
	}

	public static int getCoordZ(Object chunkCoordinates) {
		return ((ChunkCoordinates) chunkCoordinates).z;
	}

	public static boolean isCoord(Object chunkCoordinates) {
		return chunkCoordinates instanceof ChunkCoordinates;
	}

	public static Object newPosition(int x, int y, int z) {
		return new ChunkPosition(x, y, z);
	}

	public static IntVector3 getPosition(Object chunkPosition) {
		ChunkPosition position = (ChunkPosition) chunkPosition;
		return new IntVector3(position.x, position.y, position.z);
	}

	public static int getPositionX(Object chunkPosition) {
		return ((ChunkPosition) chunkPosition).x;
	}

	public static int getPositionY(Object chunkPosition) {
		return ((ChunkPosition) chunkPosition).y;
	}

	public static int getPositionZ(Object chunkPosition) {
		return ((ChunkPosition) chunkPosition).z;
	}

	public static boolean isPosition(Object chunkPosition) {
		return chunkPosition instanceof ChunkPosition;
	}
}
