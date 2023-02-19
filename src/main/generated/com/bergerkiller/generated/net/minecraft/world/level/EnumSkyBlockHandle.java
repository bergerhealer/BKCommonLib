package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.EnumSkyBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.EnumSkyBlock")
public abstract class EnumSkyBlockHandle extends Template.Handle {
    /** @See {@link EnumSkyBlockClass} */
    public static final EnumSkyBlockClass T = Template.Class.create(EnumSkyBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
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
    @Template.Readonly
    public abstract int getBrightness();
    /**
     * Stores class members for <b>net.minecraft.world.level.EnumSkyBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumSkyBlockClass extends Template.Class<EnumSkyBlockHandle> {
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> SKY = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();
        public final Template.EnumConstant.Converted<EnumSkyBlockHandle> BLOCK = new Template.EnumConstant.Converted<EnumSkyBlockHandle>();

        @Template.Readonly
        public final Template.Field.Integer brightness = new Template.Field.Integer();

    }

}

