package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.CompoundTag</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.CompoundTag")
public abstract class CompoundTagHandle extends TagHandle {
    /** @see CompoundTagClass */
    public static final CompoundTagClass T = Template.Class.create(CompoundTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CompoundTagHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static CompoundTagHandle createEmpty() {
        return T.createEmpty.invoke();
    }

    public static CompoundTagHandle create(Map<String, ?> map) {
        return T.create.invoke(map);
    }

    public abstract boolean isEmpty();
    public abstract int size();
    public abstract Set<String> getKeys();
    public abstract void remove(String key);
    public abstract TagHandle put(String key, TagHandle value);
    public abstract TagHandle get(String key);
    public abstract boolean containsKey(String key);
    public CompoundTagHandle clone() {
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
        Map<String, TagHandle> values = getData();
        str.append("TagCompound: ").append(values.size()).append(" entries {");
        for (Map.Entry<String, TagHandle> entry : values.entrySet()) {
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
    public abstract Map<String, TagHandle> getData();
    /**
     * Stores class members for <b>net.minecraft.nbt.CompoundTag</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CompoundTagClass extends Template.Class<CompoundTagHandle> {
        @Template.Readonly
        public final Template.Field.Converted<Map<String, TagHandle>> data = new Template.Field.Converted<Map<String, TagHandle>>();

        public final Template.StaticMethod.Converted<CompoundTagHandle> createEmpty = new Template.StaticMethod.Converted<CompoundTagHandle>();
        public final Template.StaticMethod.Converted<CompoundTagHandle> create = new Template.StaticMethod.Converted<CompoundTagHandle>();

        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Set<String>> getKeys = new Template.Method<Set<String>>();
        public final Template.Method.Converted<Void> remove = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<TagHandle> put = new Template.Method.Converted<TagHandle>();
        public final Template.Method.Converted<TagHandle> get = new Template.Method.Converted<TagHandle>();
        public final Template.Method<Boolean> containsKey = new Template.Method<Boolean>();

    }

}

