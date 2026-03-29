package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.LightLayer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.LightLayer")
public abstract class LightLayerHandle extends Template.Handle {
    /** @see LightLayerClass */
    public static final LightLayerClass T = Template.Class.create(LightLayerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final LightLayerHandle SKY = T.SKY.getSafe();
    public static final LightLayerHandle BLOCK = T.BLOCK.getSafe();
    /* ============================================================================== */

    public static LightLayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int ordinal() {
        return ((Enum<?>) getRaw()).ordinal();
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.LightLayer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LightLayerClass extends Template.Class<LightLayerHandle> {
        public final Template.EnumConstant.Converted<LightLayerHandle> SKY = new Template.EnumConstant.Converted<LightLayerHandle>();
        public final Template.EnumConstant.Converted<LightLayerHandle> BLOCK = new Template.EnumConstant.Converted<LightLayerHandle>();

    }

}

