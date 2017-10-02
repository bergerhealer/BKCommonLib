package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.AttributeMapServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class AttributeMapServerHandle extends Template.Handle {
    /** @See {@link AttributeMapServerClass} */
    public static final AttributeMapServerClass T = new AttributeMapServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AttributeMapServerHandle.class, "net.minecraft.server.AttributeMapServer");

    /* ============================================================================== */

    public static AttributeMapServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final AttributeMapServerHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public abstract Collection<Object> attributes();
    /**
     * Stores class members for <b>net.minecraft.server.AttributeMapServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeMapServerClass extends Template.Class<AttributeMapServerHandle> {
        public final Template.Constructor.Converted<AttributeMapServerHandle> constr = new Template.Constructor.Converted<AttributeMapServerHandle>();

        public final Template.Method.Converted<Collection<Object>> attributes = new Template.Method.Converted<Collection<Object>>();

    }

}

