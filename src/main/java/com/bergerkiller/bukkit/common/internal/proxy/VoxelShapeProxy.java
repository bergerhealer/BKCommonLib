package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;

/**
 * Emulates the behavior of the VoxelShape logic introduced in MC 1.13 using a
 * simple list of AABB objects.
 */
public class VoxelShapeProxy {
    public static final VoxelShapeProxy EMPTY = new VoxelShapeProxy(Collections.emptyList());
    private static final DuplexConverter<Object, AxisAlignedBBHandle> toAABBHandle = LogicUtil.unsafeCast(Conversion.findDuplex(
            CommonUtil.getClass("net.minecraft.world.phys.AxisAlignedBB"), AxisAlignedBBHandle.class));
    private final List<AxisAlignedBBHandle> _aabb;

    private VoxelShapeProxy(List<AxisAlignedBBHandle> aabbObjectHandles) {
        this._aabb = aabbObjectHandles;
    }

    public static VoxelShapeProxy fromAABBHandles(List<AxisAlignedBBHandle> aabbObjectHandles) {
        return new VoxelShapeProxy(aabbObjectHandles);
    }

    public static VoxelShapeProxy fromNMSAABB(List<?> aabbObjects) {
        return new VoxelShapeProxy(new ConvertingList<AxisAlignedBBHandle>(aabbObjects, toAABBHandle));
    }

    public double traceXAxis(AxisAlignedBBHandle boundingBox, double coordinate) {
        int k = 0;
        for (int l = _aabb.size(); k < l; ++k) {
            coordinate = _aabb.get(k).calcSomeX(boundingBox, coordinate);
        }
        return coordinate;
    }

    public double traceYAxis(AxisAlignedBBHandle boundingBox, double coordinate) {
        int k = 0;
        for (int l = _aabb.size(); k < l; ++k) {
            coordinate = _aabb.get(k).calcSomeY(boundingBox, coordinate);
        }
        return coordinate;
    }

    public double traceZAxis(AxisAlignedBBHandle boundingBox, double coordinate) {
        int k = 0;
        for (int l = _aabb.size(); k < l; ++k) {
            coordinate = _aabb.get(k).calcSomeZ(boundingBox, coordinate);
        }
        return coordinate;
    }

    public boolean isEmpty() {
        return this._aabb.isEmpty();
    }

    public List<AxisAlignedBBHandle> getCubes() {
        return this._aabb;
    }

    public AxisAlignedBBHandle getBoundingBox() {
        if (this._aabb.isEmpty()) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        } else if (this._aabb.size() == 1) {
            return this._aabb.get(0);
        }

        // Find minimum and maximum z/y/z coordinates of all bounding boxes
        Iterator<AxisAlignedBBHandle> iter = this._aabb.iterator();
        double minX, minY, minZ, maxX, maxY, maxZ;
        {
            AxisAlignedBBHandle first = iter.next();
            minX = first.getMinX();
            minY = first.getMinY();
            minZ = first.getMinZ();
            maxX = first.getMaxX();
            maxY = first.getMaxY();
            maxZ = first.getMaxZ();
        }
        while (iter.hasNext()) {
            AxisAlignedBBHandle other = iter.next();
            minX = Math.min(minX, other.getMinX());
            minY = Math.min(minY, other.getMinY());
            minZ = Math.min(minZ, other.getMinZ());
            maxX = Math.max(maxX, other.getMaxX());
            maxY = Math.max(maxY, other.getMaxY());
            maxZ = Math.max(maxZ, other.getMaxZ());
        }
        return AxisAlignedBBHandle.createNew(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
