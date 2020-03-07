package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BaseBlockPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BaseBlockPositionHandle extends Template.Handle {
    /** @See {@link BaseBlockPositionClass} */
    public static final BaseBlockPositionClass T = new BaseBlockPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BaseBlockPositionHandle.class, "net.minecraft.server.BaseBlockPosition", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static BaseBlockPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getX();
    public abstract int getY();
    public abstract int getZ();
    public abstract IntVector3 toIntVector3();
    @Template.Readonly
    public abstract int getField_x();
    @Template.Readonly
    public abstract int getField_y();
    @Template.Readonly
    public abstract int getField_z();
    /**
     * Stores class members for <b>net.minecraft.server.BaseBlockPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BaseBlockPositionClass extends Template.Class<BaseBlockPositionHandle> {
        @Template.Readonly
        public final Template.Field.Integer field_x = new Template.Field.Integer();
        @Template.Readonly
        public final Template.Field.Integer field_y = new Template.Field.Integer();
        @Template.Readonly
        public final Template.Field.Integer field_z = new Template.Field.Integer();

        public final Template.Method<Integer> getX = new Template.Method<Integer>();
        public final Template.Method<Integer> getY = new Template.Method<Integer>();
        public final Template.Method<Integer> getZ = new Template.Method<Integer>();
        public final Template.Method<IntVector3> toIntVector3 = new Template.Method<IntVector3>();

    }

}

