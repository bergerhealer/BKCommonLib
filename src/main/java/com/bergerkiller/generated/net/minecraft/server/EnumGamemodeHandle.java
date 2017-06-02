package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumGamemodeHandle extends Template.Handle {
    public static final EnumGamemodeClass T = new EnumGamemodeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumGamemodeHandle.class, "net.minecraft.server.EnumGamemode");

    /* ============================================================================== */

    public static EnumGamemodeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumGamemodeHandle handle = new EnumGamemodeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static EnumGamemodeHandle getById(int id) {
        return T.getById.invokeVA(id);
    }

    public int getId() {
        return T.id.getInteger(instance);
    }

    public void setId(int value) {
        T.id.setInteger(instance, value);
    }

    public static final class EnumGamemodeClass extends Template.Class<EnumGamemodeHandle> {
        public final Template.Field.Integer id = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<EnumGamemodeHandle> getById = new Template.StaticMethod.Converted<EnumGamemodeHandle>();

    }

}

