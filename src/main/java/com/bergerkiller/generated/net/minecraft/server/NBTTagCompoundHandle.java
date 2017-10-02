package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NBTTagCompound</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class NBTTagCompoundHandle extends NBTBaseHandle {
    /** @See {@link NBTTagCompoundClass} */
    public static final NBTTagCompoundClass T = new NBTTagCompoundClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTTagCompoundHandle.class, "net.minecraft.server.NBTTagCompound");

    /* ============================================================================== */

    public static NBTTagCompoundHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Set<String> getKeys() {
        return T.getKeys.invoke(getRaw());
    }

    public void remove(String key) {
        T.remove.invoke(getRaw(), key);
    }

    public void set(String key, NBTBaseHandle value) {
        T.set.invoke(getRaw(), key, value);
    }

    public NBTBaseHandle get(String key) {
        return T.get.invoke(getRaw(), key);
    }

    public boolean contains(String key) {
        return T.contains.invoke(getRaw(), key);
    }

    public boolean isEmpty() {
        return T.isEmpty.invoke(getRaw());
    }


    public int size() {
        return ((java.util.Map<?, ?>) T.map.raw.get(getRaw())).size();
    }
    public Map<String, NBTBaseHandle> getMap() {
        return T.map.get(getRaw());
    }

    public void setMap(Map<String, NBTBaseHandle> value) {
        T.map.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.NBTTagCompound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagCompoundClass extends Template.Class<NBTTagCompoundHandle> {
        public final Template.Field.Converted<Map<String, NBTBaseHandle>> map = new Template.Field.Converted<Map<String, NBTBaseHandle>>();

        public final Template.Method<Set<String>> getKeys = new Template.Method<Set<String>>();
        public final Template.Method<Void> remove = new Template.Method<Void>();
        public final Template.Method.Converted<Void> set = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method<Boolean> contains = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }

}

