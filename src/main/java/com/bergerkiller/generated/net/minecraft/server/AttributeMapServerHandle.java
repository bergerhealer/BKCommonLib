package com.bergerkiller.generated.net.minecraft.server;

import java.util.Collection;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class AttributeMapServerHandle extends Template.Handle {
    public static final AttributeMapServerClass T = new AttributeMapServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AttributeMapServerHandle.class, "net.minecraft.server.AttributeMapServer");


    /* ============================================================================== */

    public static AttributeMapServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        AttributeMapServerHandle handle = new AttributeMapServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Collection<Object> attributes() {
        return T.attributes.invoke(instance);
    }

    public static final class AttributeMapServerClass extends Template.Class<AttributeMapServerHandle> {
        public final Template.Method.Converted<Collection<Object>> attributes = new Template.Method.Converted<Collection<Object>>();

    }
}
