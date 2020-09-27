package com.bergerkiller.bukkit.common.internal.logic;

import java.util.BitSet;
import java.util.Set;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.bases.IntVector3;

public class RegionHandlerDisabled extends RegionHandler {
    private final Throwable reason;

    public RegionHandlerDisabled(Throwable reason) {
        this.reason = reason;
    }

    @Override
    public boolean isSupported(World world) {
        return false;
    }

    private UnsupportedOperationException fail() {
        return new UnsupportedOperationException("Region handler is not supported", this.reason);
    }

    @Override
    public void forceInitialization() {
        throw fail();
    }

    @Override
    public void closeStreams(World world) {
        throw fail();
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        throw fail();
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        throw fail();
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        throw fail();
    }
}
