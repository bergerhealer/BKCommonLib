package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NBTTagList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class NBTTagListHandle extends NBTBaseHandle {
    /** @See {@link NBTTagListClass} */
    public static final NBTTagListClass T = new NBTTagListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTTagListHandle.class, "net.minecraft.server.NBTTagList");

    /* ============================================================================== */

    public static NBTTagListHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NBTTagListHandle handle = new NBTTagListHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void add(NBTBaseHandle value) {
        T.add.invoke(instance, value);
    }

    public int size() {
        return T.size.invoke(instance);
    }

    public NBTBaseHandle get(int index) {
        return T.get.invoke(instance, index);
    }

    public List<NBTBaseHandle> getList() {
        return T.list.get(instance);
    }

    public void setList(List<NBTBaseHandle> value) {
        T.list.set(instance, value);
    }

    public byte getType() {
        return T.type.getByte(instance);
    }

    public void setType(byte value) {
        T.type.setByte(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.NBTTagList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagListClass extends Template.Class<NBTTagListHandle> {
        public final Template.Field.Converted<List<NBTBaseHandle>> list = new Template.Field.Converted<List<NBTBaseHandle>>();
        public final Template.Field.Byte type = new Template.Field.Byte();

        public final Template.Method.Converted<Void> add = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();

    }

}

