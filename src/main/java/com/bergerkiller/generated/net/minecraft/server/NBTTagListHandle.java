package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NBTTagList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class NBTTagListHandle extends NBTBaseHandle {
    /** @See {@link NBTTagListClass} */
    public static final NBTTagListClass T = new NBTTagListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTTagListHandle.class, "net.minecraft.server.NBTTagList");

    /* ============================================================================== */

    public static NBTTagListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean add(NBTBaseHandle value);
    public abstract int size();
    public abstract NBTBaseHandle get(int index);
    public abstract List<NBTBaseHandle> getList();
    public abstract void setList(List<NBTBaseHandle> value);
    public abstract byte getType();
    public abstract void setType(byte value);
    /**
     * Stores class members for <b>net.minecraft.server.NBTTagList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagListClass extends Template.Class<NBTTagListHandle> {
        public final Template.Field.Converted<List<NBTBaseHandle>> list = new Template.Field.Converted<List<NBTBaseHandle>>();
        public final Template.Field.Byte type = new Template.Field.Byte();

        public final Template.Method.Converted<Boolean> add = new Template.Method.Converted<Boolean>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();

    }

}

