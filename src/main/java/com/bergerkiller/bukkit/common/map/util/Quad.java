package com.bergerkiller.bukkit.common.map.util;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.map.MapTexture;

public class Quad implements Comparable<Quad> {
    public Vector3f p0 = new Vector3f();
    public Vector3f p1 = new Vector3f();
    public Vector3f p2 = new Vector3f();
    public Vector3f p3 = new Vector3f();
    public boolean set = false;
    public BlockFace face;
    public MapTexture texture = null;

    public Quad() {
    }

    public Quad(BlockFace face, Vector3f from, Vector3f to, MapTexture texture) {
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
            set = true;
        }
        if (face == BlockFace.DOWN) {
            p1.x = from.x;
            p1.z = to.z;

            p2.x = to.x;
            p2.z = to.z;

            p3.x = to.x;
            p3.z = from.z;

            p0.x = from.x;
            p0.z = from.z;
            
            
            p0.y = p1.y = p2.y = p3.y = from.y;
            set = true;
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
            set = true;
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
            set = true;
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
            set = true;
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
            set = true;
        }
    }

    public float depth() {
        float d = p0.y;
        if (p1.y < d) d = p1.y;
        if (p2.y < d) d = p2.y;
        if (p3.y < d) d = p3.y;
        return d;
        
        //return (p0.y + p1.y + p2.y + p3.y) / 4.0f;
    }

    @Override
    public int compareTo(Quad o) {
        return Float.compare(this.depth(), o.depth());
    }
}
