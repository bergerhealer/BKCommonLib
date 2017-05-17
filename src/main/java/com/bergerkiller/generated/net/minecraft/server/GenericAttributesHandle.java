package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class GenericAttributesHandle extends Template.Handle {
    public static final GenericAttributesClass T = new GenericAttributesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(GenericAttributesHandle.class, "net.minecraft.server.GenericAttributes");

    public static final Object FOLLOW_RANGE = T.FOLLOW_RANGE.getSafe();
    public static final Object MOVEMENT_SPEED = T.MOVEMENT_SPEED.getSafe();

    /* ============================================================================== */

    public static GenericAttributesHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        GenericAttributesHandle handle = new GenericAttributesHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class GenericAttributesClass extends Template.Class<GenericAttributesHandle> {
        public final Template.StaticField.Converted<Object> FOLLOW_RANGE = new Template.StaticField.Converted<Object>();
        public final Template.StaticField.Converted<Object> MOVEMENT_SPEED = new Template.StaticField.Converted<Object>();

    }
}
