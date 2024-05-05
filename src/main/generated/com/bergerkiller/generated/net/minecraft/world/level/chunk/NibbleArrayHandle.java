package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.chunk.NibbleArray</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.chunk.NibbleArray")
public abstract class NibbleArrayHandle extends Template.Handle {
    /** @see NibbleArrayClass */
    public static final NibbleArrayClass T = Template.Class.create(NibbleArrayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NibbleArrayHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final NibbleArrayHandle createNew() {
        return T.constr.newInstance();
    }

    public static final NibbleArrayHandle createNew(byte[] data) {
        return T.constr_data.newInstance(data);
    }

    /* ============================================================================== */

    public abstract int get(int x, int y, int z);
    public abstract void set(int x, int y, int z, int nibbleValue);
    public abstract byte[] getData();
    public void fill(int nibbleValue) {
        java.util.Arrays.fill(getData(), (byte) (nibbleValue & 0xF));
    }

    public boolean dataEquals(NibbleArrayHandle other) {
        return java.util.Arrays.equals(this.getData(), other.getData());
    }
    public abstract byte[] getDataField();
    public abstract void setDataField(byte[] value);
    /**
     * Stores class members for <b>net.minecraft.world.level.chunk.NibbleArray</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NibbleArrayClass extends Template.Class<NibbleArrayHandle> {
        public final Template.Constructor.Converted<NibbleArrayHandle> constr = new Template.Constructor.Converted<NibbleArrayHandle>();
        public final Template.Constructor.Converted<NibbleArrayHandle> constr_data = new Template.Constructor.Converted<NibbleArrayHandle>();

        public final Template.Field<byte[]> dataField = new Template.Field<byte[]>();

        public final Template.Method<Integer> get = new Template.Method<Integer>();
        public final Template.Method<Void> set = new Template.Method<Void>();
        public final Template.Method<byte[]> getData = new Template.Method<byte[]>();

    }

}

