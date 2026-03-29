package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.ListTag</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.ListTag")
public abstract class ListTagHandle extends TagHandle {
    /** @see ListTagClass */
    public static final ListTagClass T = Template.Class.create(ListTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ListTagHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ListTagHandle createEmpty() {
        return T.createEmpty.invoke();
    }

    public static ListTagHandle create(Collection<?> data) {
        return T.create.invoke(data);
    }

    public abstract int size();
    public abstract boolean isEmpty();
    public abstract byte getElementTypeId();
    public abstract TagHandle get_at(int index);
    public abstract void clear();
    public abstract TagHandle set_at(int index, TagHandle nbt_value);
    public abstract TagHandle remove_at(int index);
    public abstract void add_at(int index, TagHandle value);
    public abstract boolean add(TagHandle value);
    public ListTagHandle clone() {
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
        List<TagHandle> values = getData();
        str.append("TagList: ").append(values.size()).append(" entries [");
        for (TagHandle value : values) {
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
    public abstract List<TagHandle> getData();
    /**
     * Stores class members for <b>net.minecraft.nbt.ListTag</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ListTagClass extends Template.Class<ListTagHandle> {
        @Template.Readonly
        public final Template.Field.Converted<List<TagHandle>> data = new Template.Field.Converted<List<TagHandle>>();

        public final Template.StaticMethod.Converted<ListTagHandle> createEmpty = new Template.StaticMethod.Converted<ListTagHandle>();
        public final Template.StaticMethod.Converted<ListTagHandle> create = new Template.StaticMethod.Converted<ListTagHandle>();

        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<Byte> getElementTypeId = new Template.Method.Converted<Byte>();
        public final Template.Method.Converted<TagHandle> get_at = new Template.Method.Converted<TagHandle>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method.Converted<TagHandle> set_at = new Template.Method.Converted<TagHandle>();
        public final Template.Method.Converted<TagHandle> remove_at = new Template.Method.Converted<TagHandle>();
        public final Template.Method.Converted<Void> add_at = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> add = new Template.Method.Converted<Boolean>();

    }

}

