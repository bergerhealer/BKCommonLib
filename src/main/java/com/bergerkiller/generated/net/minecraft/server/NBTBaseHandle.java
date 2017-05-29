package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class NBTBaseHandle extends Template.Handle {
    public static final NBTBaseClass T = new NBTBaseClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTBaseHandle.class, "net.minecraft.server.NBTBase");


    /* ============================================================================== */

    public static NBTBaseHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NBTBaseHandle handle = new NBTBaseHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public byte getTypeId() {
        return T.getTypeId.invoke(instance);
    }

    public NBTBaseHandle clone() {
        return T.clone.invoke(instance);
    }

    public static final class NBTBaseClass extends Template.Class<NBTBaseHandle> {
        public final Template.Method<Byte> getTypeId = new Template.Method<Byte>();
        public final Template.Method.Converted<NBTBaseHandle> clone = new Template.Method.Converted<NBTBaseHandle>();

    }
}
