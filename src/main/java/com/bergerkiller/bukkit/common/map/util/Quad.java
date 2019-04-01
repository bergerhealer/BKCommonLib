package com.bergerkiller.bukkit.common.map.util;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.math.Vector3;

/**
 * A four-side four-cornered polygon that represents a rectangle with a texture applied to it
 */
public class Quad implements Comparable<Quad>, Cloneable {
    public Vector3 p0, p1, p2, p3;
    public BlockFace face;
    public MapTexture texture;

    public Quad() {
        this.p0 = new Vector3();
        this.p1 = new Vector3();
        this.p2 = new Vector3();
        this.p3 = new Vector3();
        this.face = BlockFace.UP;
        this.texture = null;
    }

    protected Quad(Quad quad) {
        this.p0 = quad.p0.clone();
        this.p1 = quad.p1.clone();
        this.p2 = quad.p2.clone();
        this.p3 = quad.p3.clone();
        this.face = quad.face;
        this.texture = quad.texture;
    }

    public Quad(BlockFace face, Vector3 from, Vector3 to, MapTexture texture) {
        this();
        this.texture = texture;
        this.face = face;
        if (face == BlockFace.UP) {
            p1.x = from.x;
            p1.z = to.z;

            p2.x = to.x;
            p2.z = to.z;

            p3.x = to.x;
            p3.z = from.z;

            p0.x = from.x;
            p0.z = from.z;
            
            p0.y = p1.y = p2.y = p3.y = to.y;
        }
        if (face == BlockFace.DOWN) {
            p1.x = from.x;
            p1.z = from.z;

            p2.x = to.x;
            p2.z = from.z;

            p3.x = to.x;
            p3.z = to.z;

            p0.x = from.x;
            p0.z = to.z;

            p0.y = p1.y = p2.y = p3.y = from.y;
        }
        if (face == BlockFace.SOUTH) {
            p1.x = from.x;
            p1.y = from.y;

            p2.x = to.x;
            p2.y = from.y;

            p3.x = to.x;
            p3.y = to.y;

            p0.x = from.x;
            p0.y = to.y;

            p0.z = p1.z = p2.z = p3.z = to.z;
        }
        if (face == BlockFace.NORTH) {
            p1.x = to.x;
            p1.y = from.y;

            p2.x = from.x;
            p2.y = from.y;

            p3.x = from.x;
            p3.y = to.y;

            p0.x = to.x;
            p0.y = to.y;

            p0.z = p1.z = p2.z = p3.z = from.z;
        }
        if (face == BlockFace.EAST) {
            p1.y = from.y;
            p1.z = to.z;

            p2.y = from.y;
            p2.z = from.z;

            p3.y = to.y;
            p3.z = from.z;

            p0.y = to.y;
            p0.z = to.z;

            p0.x = p1.x = p2.x = p3.x = to.x;
        }
        if (face == BlockFace.WEST) {
            p1.y = from.y;
            p1.z = from.z;

            p2.y = from.y;
            p2.z = to.z;

            p3.y = to.y;
            p3.z = to.z;

            p0.y = to.y;
            p0.z = from.z;

            p0.x = p1.x = p2.x = p3.x = from.x;
        }
    }

    /**
     * Replaces points that equal points in this quad with the one of another quad.
     * By merging the 6 quads of a cube can be manipulated with just 8 points.
     * 
     * @param quad to merge points with
     */
    public final void mergePoints(Quad quad) {
        mergePoint(quad.p0);
        mergePoint(quad.p1);
        mergePoint(quad.p2);
        mergePoint(quad.p3);
    }

    private final void mergePoint(Vector3 p) {
        if (p0.equals(p)) p0 = p;
        if (p1.equals(p)) p1 = p;
        if (p2.equals(p)) p2 = p;
        if (p3.equals(p)) p3 = p;
    }

    public double depth() {
        double d = p0.y;
        if (p1.y < d) d = p1.y;
        if (p2.y < d) d = p2.y;
        if (p3.y < d) d = p3.y;
        return d;
        
        //return (p0.y + p1.y + p2.y + p3.y) / 4.0f;
    }

    @Override
    public int compareTo(Quad o) {
        return Double.compare(this.depth(), o.depth());
    }

    @Override
    public Quad clone() {
        return new Quad(this);
    }
}
