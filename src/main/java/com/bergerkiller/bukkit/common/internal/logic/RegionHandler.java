package com.bergerkiller.bukkit.common.internal.logic;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

public abstract class RegionHandler implements LazyInitializedObject, LibraryComponent {
    public static final RegionHandler INSTANCE = new RegionHandlerSelector();

    /**
     * Gets whether the world specified is supported by this region handler
     * 
     * @param world
     * @return True if this world is supported
     */
    public abstract boolean isSupported(World world);

    /**
     * Closes all the open files for a world, so that the files can be
     * moved or deleted on the filesystem with no problems.
     * 
     * @param world
     */
    public abstract void closeStreams(World world);

    /**
     * Gets all the loadable region coordinates that have one of the given flat X/Z coordinates.
     * This can be used to quickly determine the available Y-coordinates of a large number
     * of chunks.
     * 
     * @param world
     * @param regionXZCoordinates
     * @return region coordinates of the world with one of the given x/z coordinates
     */
    public abstract Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates);

    /**
     * Gets all region indices for loadable regions of a world.
     * Regions are 32x32x32 areas of chunks. On vanilla Minecraft,
     * the Y component will always be 0, because it is limited to y=256.
     * On forge with cubic chunks installed, the y component can also
     * increase.
     * 
     * @param world
     * @return region coordinates of the world
     */
    public abstract Set<IntVector3> getRegions3(World world);

    /**
     * Gets all region indices for loadable regions of a world.<br>
     * <br>
     * <b>Deprecated: use {@link #getRegions3(World)} instead
     * to support servers with infinite Y-coordinate generation</b>
     * 
     * @param world
     * @return region indices
     */
    @Deprecated
    public final Set<IntVector2> getRegions(World world) {
        Set<IntVector3> coords_3d = getRegions3(world);
        Set<IntVector2> coords_2d = new HashSet<IntVector2>(coords_3d.size());
        for (IntVector3 coord : coords_3d) {
            coords_2d.add(coord.toIntVector2());
        }
        return coords_2d;
    }

    /**
     * Gets a bitset of length 1024 containing a True/False of which chunks
     * in a region exists.
     * 
     * @param world
     * @param rx - region X-coordinate
     * @param ry - region Y-coordinate
     * @param rz - region Z-coordinate
     * @return bitset of all chunks in a region that exist
     */
    public abstract BitSet getRegionChunks3(World world, int rx, int ry, int rz);

    /**
     * Gets a bitset of length 1024 containing a True/False of which chunks
     * in a region exists.<br>
     * <br>
     * <b>Deprecated: use {@link #getRegionChunks3(World, int, int, int)} instead
     * to support servers with infinite Y-coordinate generation</b>
     * 
     * @param world
     * @param rx - region X-coordinate
     * @param rz - region Z-coordinate
     * @return bitset of all chunks in a region that exist
     */
    @Deprecated
    public final BitSet getRegionChunks(World world, int rx, int rz) {
        return getRegionChunks3(world, rx, 0, rz);
    }

    /**
     * Gets whether a particular chunk exists on disk and can be loaded,
     * instead of generating it.
     * 
     * @param world
     * @param cx
     * @param cz
     * @return True if the chunk is saved and can be loaded
     */
    public abstract boolean isChunkSaved(World world, int cx, int cz);

    /**
     * Gets the minimum Block Y-coordinates possible on a world
     *
     * @param world
     * @return World minimum height
     */
    public int getMinHeight(World world) {
        return 0;
    }

    /**
     * Gets the maximum Block Y-coordinates possible on a world
     *
     * @param world
     * @return World maximum height
     */
    public int getMaxHeight(World world) {
        return world.getMaxHeight();
    }
}
