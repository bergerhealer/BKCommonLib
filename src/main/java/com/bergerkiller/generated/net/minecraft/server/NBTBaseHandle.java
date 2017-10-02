package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NBTBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class NBTBaseHandle extends Template.Handle {
    /** @See {@link NBTBaseClass} */
    public static final NBTBaseClass T = new NBTBaseClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTBaseHandle.class, "net.minecraft.server.NBTBase");

    /* ============================================================================== */

    public static NBTBaseHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public byte getTypeId() {
        return T.getTypeId.invoke(getRaw());
    }

    public NBTBaseHandle clone() {
        return T.clone.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.NBTBase</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTBaseClass extends Template.Class<NBTBaseHandle> {
        public final Template.Method<Byte> getTypeId = new Template.Method<Byte>();
        public final Template.Method.Converted<NBTBaseHandle> clone = new Template.Method.Converted<NBTBaseHandle>();

    }

}

