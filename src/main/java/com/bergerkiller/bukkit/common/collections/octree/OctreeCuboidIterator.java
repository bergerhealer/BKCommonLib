package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Implementation of {@link OctreeIterator} that only returns results that falls
 * inside a cuboid area.
 * 
 * @param <T>
 */
public class OctreeCuboidIterator<T> extends OctreeIterator<T> {
    private final IntVector3 min;
    private final IntVector3 max;

    public static int NUM_INTERSECTS = 0;

    public OctreeCuboidIterator(Octree<T> tree, IntVector3 min, IntVector3 max) {
        super(tree);
        this.min = min;
        this.max = max;
    }

    @Override
    protected Intersection intersect() {
        NUM_INTERSECTS++;

        int min_x = getX();
        int min_y = getY();
        int min_z = getZ();
        int mask = getScale()-1;
        int max_x = min_x | mask;
        int max_y = min_y | mask;
        int max_z = min_z | mask;

        if (min_x > this.max.x || min_y > this.max.y || min_z > this.max.z) {
            return Intersection.OUTSIDE;
        }
        if (max_x < this.min.x || max_y < this.min.y || max_z < this.min.z) {
            return Intersection.OUTSIDE;
        }

        if (min_x >= this.min.x && min_y >= this.min.y && min_z >= this.min.z &&
            max_x <= this.max.x && max_y <= this.max.y && max_z <= this.max.z)
        {
            return Intersection.INSIDE;
        }

        return Intersection.PARTIAL;
    }
}
