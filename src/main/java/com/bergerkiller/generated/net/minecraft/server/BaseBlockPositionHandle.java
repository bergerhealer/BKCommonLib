package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BaseBlockPositionHandle extends Template.Handle {
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

    public static final class BaseBlockPositionClass extends Template.Class<BaseBlockPositionHandle> {
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

    }

}

