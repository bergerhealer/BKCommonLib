package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.Attribute</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.Attribute")
public abstract class AttributeHandle extends Template.Handle {
    /** @see AttributeClass */
    public static final AttributeClass T = Template.Class.create(AttributeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AttributeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getDescriptionId();
    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.Attribute</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeClass extends Template.Class<AttributeHandle> {
        public final Template.Method<String> getDescriptionId = new Template.Method<String>();

    }

}

