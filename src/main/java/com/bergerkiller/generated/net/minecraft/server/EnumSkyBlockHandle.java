package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumSkyBlockHandle extends Template.Handle {
    public static final EnumSkyBlockClass T = new EnumSkyBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumSkyBlockHandle.class, "net.minecraft.server.EnumSkyBlock");

    public static final EnumSkyBlockHandle SKY = T.SKY.getSafe();
    public static final EnumSkyBlockHandle BLOCK = T.BLOCK.getSafe();
    /* ============================================================================== */

    public static EnumSkyBlockHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumSkyBlockHandle handle = new EnumSkyBlockHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public int ordinal() {
        return ((Enum<?>) instance).ordinal();
    }
    public int getBrightness() {
        return T.brightness.getInteger(instance);
    }

    public void setBrightness(int value) {
        T.brightness.setInteger(instance, value);
    }

    public static final class EnumSkyBlockClass extends Template.Class<EnumSkyBlockHandle> {
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> SKY = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> BLOCK = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();

        public final Template.Field.Integer brightness = new Template.Field.Integer();

    }

}

