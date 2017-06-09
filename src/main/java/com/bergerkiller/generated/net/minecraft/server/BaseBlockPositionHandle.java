package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BaseBlockPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class BaseBlockPositionHandle extends Template.Handle {
    /** @See {@link BaseBlockPositionClass} */
    public static final BaseBlockPositionClass T = new BaseBlockPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BaseBlockPositionHandle.class, "net.minecraft.server.BaseBlockPosition");

    /* ============================================================================== */

    public static BaseBlockPositionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BaseBlockPositionHandle handle = new BaseBlockPositionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getX() {
        return T.x.getInteger(instance);
    }

    public void setX(int value) {
        T.x.setInteger(instance, value);
    }

    public int getY() {
        return T.y.getInteger(instance);
    }

    public void setY(int value) {
        T.y.setInteger(instance, value);
    }

    public int getZ() {
        return T.z.getInteger(instance);
    }

    public void setZ(int value) {
        T.z.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.BaseBlockPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BaseBlockPositionClass extends Template.Class<BaseBlockPositionHandle> {
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

    }

}

