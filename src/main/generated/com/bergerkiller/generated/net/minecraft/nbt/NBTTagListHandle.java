package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.NBTTagList")
public abstract class NBTTagListHandle extends NBTBaseHandle {
    /** @see NBTTagListClass */
    public static final NBTTagListClass T = Template.Class.create(NBTTagListClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NBTTagListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static NBTTagListHandle createEmpty() {
        return T.createEmpty.invoke();
    }

    public static NBTTagListHandle create(Collection<?> data) {
        return T.create.invoke(data);
    }

    public abstract int size();
    public abstract boolean isEmpty();
    public abstract byte getElementTypeId();
    public abstract NBTBaseHandle get_at(int index);
    public abstract void clear();
    public abstract NBTBaseHandle set_at(int index, NBTBaseHandle nbt_value);
    public abstract NBTBaseHandle remove_at(int index);
    public abstract void add_at(int index, NBTBaseHandle value);
    public abstract boolean add(NBTBaseHandle value);
    public NBTTagListHandle clone() {
        return createHandle(raw_clone());
    }

    public com.bergerkiller.bukkit.common.nbt.CommonTagList toCommonTag() {
        return new com.bergerkiller.bukkit.common.nbt.CommonTagList(this);
    }

    @Override
    public void toPrettyString(StringBuilder str, int indent) {
        for (int i = 0; i < indent; i++) {
            str.append("  ");
        }
        List<NBTBaseHandle> values = getData();
        str.append("TagList: ").append(values.size()).append(" entries [");
        for (NBTBaseHandle value : values) {
            str.append('\n');
            value.toPrettyString(str, indent + 1);
        }
        if (!values.isEmpty()) {
            str.append('\n');
            for (int i = 0; i < indent; i++) {
                str.append("  ");
            }
        }
        str.append(']');
    }
    @Template.Readonly
    public abstract List<NBTBaseHandle> getData();
    /**
     * Stores class members for <b>net.minecraft.nbt.NBTTagList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTTagListClass extends Template.Class<NBTTagListHandle> {
        @Template.Readonly
        public final Template.Field.Converted<List<NBTBaseHandle>> data = new Template.Field.Converted<List<NBTBaseHandle>>();

        public final Template.StaticMethod.Converted<NBTTagListHandle> createEmpty = new Template.StaticMethod.Converted<NBTTagListHandle>();
        public final Template.StaticMethod.Converted<NBTTagListHandle> create = new Template.StaticMethod.Converted<NBTTagListHandle>();

        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<Byte> getElementTypeId = new Template.Method.Converted<Byte>();
        public final Template.Method.Converted<NBTBaseHandle> get_at = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method.Converted<NBTBaseHandle> set_at = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method.Converted<NBTBaseHandle> remove_at = new Template.Method.Converted<NBTBaseHandle>();
        public final Template.Method.Converted<Void> add_at = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> add = new Template.Method.Converted<Boolean>();

    }

}

