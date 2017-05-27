package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class NibbleArrayHandle extends Template.Handle {
    public static final NibbleArrayClass T = new NibbleArrayClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NibbleArrayHandle.class, "net.minecraft.server.NibbleArray");


    /* ============================================================================== */

    public static NibbleArrayHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NibbleArrayHandle handle = new NibbleArrayHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final NibbleArrayHandle createNew() {
        return T.constr.newInstance();
    }

    public static final NibbleArrayHandle createNew(byte[] data) {
        return T.constr_data.newInstance(data);
    }

    /* ============================================================================== */

    public int get(int x, int y, int z) {
        return T.get.invoke(instance, x, y, z);
    }

    public void set(int x, int y, int z, int nibbleValue) {
        T.set.invoke(instance, x, y, z, nibbleValue);
    }

    public byte[] getData() {
        return T.data.get(instance);
    }

    public void setData(byte[] value) {
        T.data.set(instance, value);
    }

    public static final class NibbleArrayClass extends Template.Class<NibbleArrayHandle> {
        public final Template.Constructor.Converted<NibbleArrayHandle> constr = new Template.Constructor.Converted<NibbleArrayHandle>();
        public final Template.Constructor.Converted<NibbleArrayHandle> constr_data = new Template.Constructor.Converted<NibbleArrayHandle>();

        public final Template.Field<byte[]> data = new Template.Field<byte[]>();

        public final Template.Method<Integer> get = new Template.Method<Integer>();
        public final Template.Method<Void> set = new Template.Method<Void>();

    }
}
