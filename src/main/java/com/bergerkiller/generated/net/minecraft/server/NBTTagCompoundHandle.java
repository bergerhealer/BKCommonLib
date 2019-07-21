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
public abstract class NBTTagCompoundHandle extends NBTBaseHandle {
    /** @See {@link NBTTagCompoundClass} */
    public static final NBTTagCompoundClass T = new NBTTagCompoundClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTTagCompoundHandle.class, "net.minecraft.server.NBTTagCompound", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static NBTTagCompoundHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static NBTBaseHandle fromMojangson(String mojangson) {
        return T.fromMojangson.invoke(mojangson);
    }

    public abstract Set<String> getKeys();
    public abstract void remove(String key);
    public abstract void set(String key, NBTBaseHandle value);
    public abstract NBTBaseHandle get(String key);
    public abstract boolean contains(String key);
    public abstract boolean isEmpty();

    public int size() {
        return ((java.util.Map<?, ?>) T.map.raw.get(getRaw())).size();
    }
    public abstract Map<String, NBTBaseHandle> getMap();
    public abstract void setMap(Map<String, NBTBaseHandle> value);
    /**
     * Stores class members for <b>net.minecraft.server.NBTTagCompound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagCompoundClass extends Template.Class<NBTTagCompoundHandle> {
        public final Template.Field.Converted<Map<String, NBTBaseHandle>> map = new Template.Field.Converted<Map<String, NBTBaseHandle>>();

        public final Template.StaticMethod.Converted<NBTBaseHandle> fromMojangson = new Template.StaticMethod.Converted<NBTBaseHandle>();

        public final Template.Method<Set<String>> getKeys = new Template.Method<Set<String>>();
        public final Template.Method<Void> remove = new Template.Method<Void>();
        public final Template.Method.Converted<Void> set = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method<Boolean> contains = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }

}

