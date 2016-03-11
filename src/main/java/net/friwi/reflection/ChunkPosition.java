package net.friwi.reflection;

import net.minecraft.server.v1_9_R1.BaseBlockPosition;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.Vec3D;

public class ChunkPosition extends BlockPosition {

    public ChunkPosition(BaseBlockPosition name) {
        super(name);
    }

    public ChunkPosition(int x, int y, int z) {
        super(x, y, z);
    }

    public ChunkPosition(Entity e) {
        super(e);
    }

    public ChunkPosition(Vec3D name) {
        super(name);
    }

    public ChunkPosition(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public int compareTo(BaseBlockPosition o) {
        return -1;
    }
}
