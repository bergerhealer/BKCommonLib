package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;

/**
 * Emulates the behavior of the VoxelShape logic introduced in MC 1.13 using a
 * simple list of AABB objects.
 */
public class VoxelShapeProxy {
    public static final VoxelShapeProxy EMPTY = new VoxelShapeProxy(Collections.emptyList());
    private static final DuplexConverter<Object, AxisAlignedBBHandle> toAABBHandle = CommonUtil.unsafeCast(Conversion.findDuplex(
            CommonUtil.getNMSClass("AxisAlignedBB"), AxisAlignedBBHandle.class));
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
}
