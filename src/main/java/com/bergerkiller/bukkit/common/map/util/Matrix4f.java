package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.math.Matrix4x4;

/**
 * Deprecated: use {@link Matrix4x4} instead
 */
@Deprecated
public class Matrix4f extends Matrix4x4 {

    public final void translate(float dx, float dy, float dz) {
        super.translate(dx, dy, dz);
    }

    public final void scale(float sx, float sy, float sz) {
        super.scale(sx, sy, sz);
    }

    public final void rotateX(float angle) {
        super.rotateX(angle);
    }

    public final void rotateY(float angle) {
        super.rotateY(angle);
    }

    public final void rotateZ(float angle) {
        super.rotateZ(angle);
    }
}
