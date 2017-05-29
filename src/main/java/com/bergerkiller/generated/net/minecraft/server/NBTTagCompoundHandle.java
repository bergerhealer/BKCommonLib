package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Set;
import java.util.Map;

public class NBTTagCompoundHandle extends NBTBaseHandle {
    public static final NBTTagCompoundClass T = new NBTTagCompoundClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTTagCompoundHandle.class, "net.minecraft.server.NBTTagCompound");


    /* ============================================================================== */

    public static NBTTagCompoundHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NBTTagCompoundHandle handle = new NBTTagCompoundHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int size() {
        return T.size.invoke(instance);
    }

    public Set<String> getKeys() {
        return T.getKeys.invoke(instance);
    }

    public void remove(String key) {
        T.remove.invoke(instance, key);
    }

    public void set(String key, NBTBaseHandle value) {
        T.set.invoke(instance, key, value);
    }

    public NBTBaseHandle get(String key) {
        return T.get.invoke(instance, key);
    }

    public boolean contains(String key) {
        return T.contains.invoke(instance, key);
    }

    public boolean isEmpty() {
        return T.isEmpty.invoke(instance);
    }

    public Map<String, NBTBaseHandle> getMap() {
        return T.map.get(instance);
    }

    public void setMap(Map<String, NBTBaseHandle> value) {
        T.map.set(instance, value);
    }

    public static final class NBTTagCompoundClass extends Template.Class<NBTTagCompoundHandle> {
        public final Template.Field.Converted<Map<String, NBTBaseHandle>> map = new Template.Field.Converted<Map<String, NBTBaseHandle>>();

        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Set<String>> getKeys = new Template.Method<Set<String>>();
        public final Template.Method<Void> remove = new Template.Method<Void>();
        public final Template.Method.Converted<Void> set = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method<Boolean> contains = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }
}
