package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagCompound</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.NBTTagCompound")
public abstract class NBTTagCompoundHandle extends NBTBaseHandle {
    /** @see NBTTagCompoundClass */
    public static final NBTTagCompoundClass T = Template.Class.create(NBTTagCompoundClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NBTTagCompoundHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static NBTTagCompoundHandle createEmpty() {
        return T.createEmpty.invoke();
    }

    public static NBTTagCompoundHandle create(Map<String, ?> map) {
        return T.create.invoke(map);
    }

    public static NBTBaseHandle fromMojangson(String mojangson) {
        return T.fromMojangson.invoke(mojangson);
    }

    public abstract boolean isEmpty();
    public abstract int size();
    public abstract Set<String> getKeys();
    public abstract void remove(String key);
    public abstract NBTBaseHandle put(String key, NBTBaseHandle value);
    public abstract NBTBaseHandle get(String key);
    public abstract boolean containsKey(String key);
    public NBTTagCompoundHandle clone() {
        return createHandle(raw_clone());
    }

    public com.bergerkiller.bukkit.common.nbt.CommonTagCompound toCommonTag() {
        return new com.bergerkiller.bukkit.common.nbt.CommonTagCompound(this);
    }

    @Override
    public void toPrettyString(StringBuilder str, int indent) {
        for (int i = 0; i < indent; i++) {
            str.append("  ");
        }
        Map<String, NBTBaseHandle> values = getData();
        str.append("TagCompound: ").append(values.size()).append(" entries {");
        for (Map.Entry<String, NBTBaseHandle> entry : values.entrySet()) {
            str.append('\n');
            for (int i = 0; i <= indent; i++) {
                str.append("  ");
            }
            str.append(entry.getKey()).append(" = ");
            int startOffset = str.length();
            entry.getValue().toPrettyString(str, indent + 1);
            str.delete(startOffset, startOffset + 2 * (indent + 1));
        }
        if (!values.isEmpty()) {
            str.append('\n');
            for (int i = 0; i < indent; i++) {
                str.append("  ");
            }
        }
        str.append('}');
    }
    @Template.Readonly
    public abstract Map<String, NBTBaseHandle> getData();
    /**
     * Stores class members for <b>net.minecraft.nbt.NBTTagCompound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagCompoundClass extends Template.Class<NBTTagCompoundHandle> {
        @Template.Readonly
        public final Template.Field.Converted<Map<String, NBTBaseHandle>> data = new Template.Field.Converted<Map<String, NBTBaseHandle>>();

        public final Template.StaticMethod.Converted<NBTTagCompoundHandle> createEmpty = new Template.StaticMethod.Converted<NBTTagCompoundHandle>();
        public final Template.StaticMethod.Converted<NBTTagCompoundHandle> create = new Template.StaticMethod.Converted<NBTTagCompoundHandle>();
        public final Template.StaticMethod.Converted<NBTBaseHandle> fromMojangson = new Template.StaticMethod.Converted<NBTBaseHandle>();

        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Set<String>> getKeys = new Template.Method<Set<String>>();
        public final Template.Method<Void> remove = new Template.Method<Void>();
        public final Template.Method.Converted<NBTBaseHandle> put = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method.Converted<NBTBaseHandle> get = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method<Boolean> containsKey = new Template.Method<Boolean>();

    }

}

