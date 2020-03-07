package com.bergerkiller.bukkit.common.map.util;

import java.util.UUID;

import com.bergerkiller.bukkit.common.internal.CommonMapUUIDStore;

/**
 * Uniquely identifies a Map tile of a map display
 */
public final class MapUUID {
    private final int tileX, tileY;
    private final UUID uuid;

    public MapUUID(UUID uuid) {
        this(uuid, 0, 0);
    }

    public MapUUID(UUID uuid, int tileX, int tileY) {
        this.uuid = uuid;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public final UUID getUUID() {
        return this.uuid;
    }

    public final int getTileX() {
        return this.tileX;
    }

    public final int getTileY() {
        return this.tileY;
    }

    public boolean isStaticUUID() {
        return CommonMapUUIDStore.isStaticMapId(this.uuid);
    }

    @Override
    public final int hashCode() {
        return (this.uuid.hashCode() << 6) + (this.tileX << 4) + (this.tileY << 2);
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MapUUID) {
            MapUUID other = (MapUUID) o;
            return uuid.equals(other.uuid) && tileX == other.tileX && tileY == other.tileY;
        } else {
            return false;
        }
    }

    @Override
    public final String toString() {
        return this.uuid.toString() + "{" + this.tileX + ", " + this.tileY + "}";
    }
}
