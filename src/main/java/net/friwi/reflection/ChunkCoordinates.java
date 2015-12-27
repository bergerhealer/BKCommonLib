package net.friwi.reflection;

public class ChunkCoordinates implements Comparable {

    public int x;
    public int y;
    public int z;

    public ChunkCoordinates() {
    }

    public ChunkCoordinates(int paramInt1, int paramInt2, int paramInt3) {
        this.x = paramInt1;
        this.y = paramInt2;
        this.z = paramInt3;
    }

    public ChunkCoordinates(ChunkCoordinates paramChunkCoordinates) {
        this.x = paramChunkCoordinates.x;
        this.y = paramChunkCoordinates.y;
        this.z = paramChunkCoordinates.z;
    }

    public boolean equals(Object paramObject) {
        if (!(paramObject instanceof ChunkCoordinates)) {
            return false;
        }

        ChunkCoordinates localChunkCoordinates = (ChunkCoordinates) paramObject;
        return (this.x == localChunkCoordinates.x) && (this.y == localChunkCoordinates.y) && (this.z == localChunkCoordinates.z);
    }

    public int hashCode() {
        return this.x + this.z << 8 + this.y << 16;
    }

    @Override
    public int compareTo(Object obj) {
        if (!(obj instanceof ChunkCoordinates)) {
            return -10000;
        }
        ChunkCoordinates paramChunkCoordinates = (ChunkCoordinates) obj;
        if (this.y == paramChunkCoordinates.y) {
            if (this.z == paramChunkCoordinates.z) {
                return this.x - paramChunkCoordinates.x;
            }
            return this.z - paramChunkCoordinates.z;
        }
        return this.y - paramChunkCoordinates.y;
    }

    public void b(int paramInt1, int paramInt2, int paramInt3) {
        this.x = paramInt1;
        this.y = paramInt2;
        this.z = paramInt3;
    }

    public float e(int paramInt1, int paramInt2, int paramInt3) {
        float f1 = this.x - paramInt1;
        float f2 = this.y - paramInt2;
        float f3 = this.z - paramInt3;
        return f1 * f1 + f2 * f2 + f3 * f3;
    }

    public float e(ChunkCoordinates paramChunkCoordinates) {
        return e(paramChunkCoordinates.x, paramChunkCoordinates.y, paramChunkCoordinates.z);
    }

    public String toString() {
        return "Pos{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }

}
