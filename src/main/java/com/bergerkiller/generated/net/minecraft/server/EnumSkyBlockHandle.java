package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumSkyBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EnumSkyBlockHandle extends Template.Handle {
    /** @See {@link EnumSkyBlockClass} */
    public static final EnumSkyBlockClass T = new EnumSkyBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumSkyBlockHandle.class, "net.minecraft.server.EnumSkyBlock");

    public static final EnumSkyBlockHandle SKY = T.SKY.getSafe();
    public static final EnumSkyBlockHandle BLOCK = T.BLOCK.getSafe();
    /* ============================================================================== */

    public static EnumSkyBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public int ordinal() {
        return ((Enum<?>) getRaw()).ordinal();
    }
    public abstract int getBrightness();
    public abstract void setBrightness(int value);
    /**
     * Stores class members for <b>net.minecraft.server.EnumSkyBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumSkyBlockClass extends Template.Class<EnumSkyBlockHandle> {
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> SKY = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> BLOCK = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();

        public final Template.Field.Integer brightness = new Template.Field.Integer();

    }

}

