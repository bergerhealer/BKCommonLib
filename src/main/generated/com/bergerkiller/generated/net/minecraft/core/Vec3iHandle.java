package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.Vec3i</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.Vec3i")
public abstract class Vec3iHandle extends Template.Handle {
    /** @see Vec3iClass */
    public static final Vec3iClass T = Template.Class.create(Vec3iClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static Vec3iHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getX();
    public abstract int getY();
    public abstract int getZ();
    public abstract boolean isPositionInBox(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax);
    public abstract IntVector3 toIntVector3();
    /**
     * Stores class members for <b>net.minecraft.core.Vec3i</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class Vec3iClass extends Template.Class<Vec3iHandle> {
        public final Template.Method<Integer> getX = new Template.Method<Integer>();
        public final Template.Method<Integer> getY = new Template.Method<Integer>();
        public final Template.Method<Integer> getZ = new Template.Method<Integer>();
        public final Template.Method<Boolean> isPositionInBox = new Template.Method<Boolean>();
        public final Template.Method<IntVector3> toIntVector3 = new Template.Method<IntVector3>();

    }

}

