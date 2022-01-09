package com.bergerkiller.bukkit.common.internal.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;

/**
 * Group of item frames that are connected together and face the same way
 */
public final class ItemFrameCluster {
    // World where this item frame cluster is at
    public final OfflineWorld world;
    // Facing of the display
    public final BlockFace facing;
    // Set of coordinates where item frames are stored
    public final Set<IntVector3> coordinates;
    // Most common ItemFrame rotation used for the display
    public final int rotation;
    // Minimum/maximum coordinates and size of the item frame coordinates in this cluster
    public final IntVector3 min_coord, max_coord;
    // Chunks that must be loaded in addition to this cluster, to make this cluster validly loaded
    public final ChunkDependency[] chunk_dependencies;
    // Resolution in rotation/facing relative space (unused)
    // public final IntVector3 size;
    // public final IntVector2 resolution;

    // A temporary builder which we use to track what chunks need to be loaded to load a cluster
    private static final ChunkDependencyBuilder BUILDER = new ChunkDependencyBuilder();

    public ItemFrameCluster(OfflineWorld world, BlockFace facing, Set<IntVector3> coordinates, int rotation) {
        this.world = world;
        this.facing = facing;
        this.coordinates = coordinates;
        this.rotation = rotation;

        if (hasMultipleTiles()) {
            // Compute minimum/maximum x and z coordinates
            Iterator<IntVector3> iter = coordinates.iterator();
            IntVector3 coord = iter.next();
            int min_x, max_x, min_y, max_y, min_z, max_z;
            min_x = max_x = coord.x; min_y = max_y = coord.y; min_z = max_z = coord.z;
            while (iter.hasNext()) {
                coord = iter.next();
                if (coord.x < min_x) min_x = coord.x;
                if (coord.y < min_y) min_y = coord.y;
                if (coord.z < min_z) min_z = coord.z;
                if (coord.x > max_x) max_x = coord.x;
                if (coord.y > max_y) max_y = coord.y;
                if (coord.z > max_z) max_z = coord.z;
            }
            min_coord = new IntVector3(min_x, min_y, min_z);
            max_coord = new IntVector3(max_x, max_y, max_z);
        } else {
            min_coord = max_coord = coordinates.iterator().next();
        }

        synchronized (BUILDER) {
            try {
                this.chunk_dependencies = BUILDER.process(facing, coordinates);
            } finally {
                BUILDER.reset();
            }
        }

        // Compute resolution (unused)
        /*
        if (hasMultipleTiles()) {
            size = max_coord.subtract(min_coord);
            if (facing.getModY() > 0) {
                // Vertical pointing up
                // We use rotation of the item frame to decide which side is up
                switch (rotation) {
                case 90:
                case 270:
                    resolution = new IntVector2(size.z+1, size.x+1);
                    break;
                case 180:
                default:
                    resolution = new IntVector2(size.x+1, size.z+1);
                    break;
                }
            } else if (facing.getModY() < 0) {
                // Vertical pointing down
                // We use rotation of the item frame to decide which side is up
                switch (rotation) {
                case 90:
                case 270:
                    resolution = new IntVector2(size.z+1, size.x+1);
                    break;
                case 180:
                default:
                    resolution = new IntVector2(size.x+1, size.z+1);
                    break;
                }
            } else {
                // On the wall
                switch (facing) {
                case NORTH:
                case SOUTH:
                    resolution = new IntVector2(size.x+1, size.y+1);
                    break;
                case EAST:
                case WEST:
                    resolution = new IntVector2(size.z+1, size.y+1);
                    break;
                default:
                    resolution = new IntVector2(1, 1);
                    break;
                }
            }
        } else {
            resolution = new IntVector2(1, 1);
            size = IntVector3.ZERO;
        }
        */
    }

    public boolean hasMultipleTiles() {
        return coordinates.size() > 1;
    }

    private static final class ChunkDependencyBuilder {
        private final LongHashSet covered = new LongHashSet();
        private final List<ChunkDependency> dependencies = new ArrayList<ChunkDependency>();

        public ChunkDependency[] process(BlockFace facing, Collection<IntVector3> coordinates) {
            // Add all chunks definitely covered by item frames, and therefore loaded
            // These are excluded as dependencies
            for (IntVector3 coordinate : coordinates) {
                covered.add(coordinate.getChunkX(), coordinate.getChunkZ());
            }

            // Go by all coordinates and if they sit at a chunk border, check that chunk is loaded
            // If it is not, add it as a dependency
            if (!FaceUtil.isAlongX(facing)) {
                for (IntVector3 coordinate : coordinates) {
                    if ((coordinate.x & 0xF) == 0x0) {
                        probe(coordinate.getChunkX(), coordinate.getChunkZ(), -1, 0);
                    } else if ((coordinate.x & 0xF) == 0xF) {
                        probe(coordinate.getChunkX(), coordinate.getChunkZ(), 1, 0);
                    }
                }
            }
            if (!FaceUtil.isAlongZ(facing)) {
                for (IntVector3 coordinate : coordinates) {
                    if ((coordinate.z & 0xF) == 0x0) {
                        probe(coordinate.getChunkX(), coordinate.getChunkZ(), 0, -1);
                    } else if ((coordinate.z & 0xF) == 0xF) {
                        probe(coordinate.getChunkX(), coordinate.getChunkZ(), 0, 1);
                    }
                }
            }

            // To array
            return dependencies.toArray(new ChunkDependency[dependencies.size()]);
        }

        public void probe(int cx, int cz, int dx, int dz) {
            int n_cx = cx + dx;
            int n_cz = cz + dz;
            if (covered.add(n_cx, n_cz)) {
                dependencies.add(new ChunkDependency(cx, cz, n_cx, n_cz));
            }
        }

        public void reset() {
            covered.clear();
            dependencies.clear();
        }
    }

    /**
     * A chunk neighbouring this cluster that must be loaded before
     * this cluster of item frames becomes active.
     * Tracks the chunk that needs to be loaded, as well as the
     * chunk the item frame is in that needs this neighbour.
     */
    public static final class ChunkDependency {
        public static final ChunkDependency NONE = new ChunkDependency();
        public final IntVector2 self;
        public final IntVector2 neighbour;

        private ChunkDependency() {
            this.self = null;
            this.neighbour = null;
        }

        public ChunkDependency(int cx, int cz, int n_cx, int n_cz) {
            this.self = new IntVector2(cx, cz);
            this.neighbour = new IntVector2(n_cx, n_cz);
        }
    }
}