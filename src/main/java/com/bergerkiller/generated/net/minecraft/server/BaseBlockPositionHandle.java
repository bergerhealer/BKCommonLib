package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BaseBlockPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BaseBlockPositionHandle extends Template.Handle {
    /** @See {@link BaseBlockPositionClass} */
    public static final BaseBlockPositionClass T = new BaseBlockPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BaseBlockPositionHandle.class, "net.minecraft.server.BaseBlockPosition");

    /* ============================================================================== */

    public static BaseBlockPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getY();
    public abstract void setY(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
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

