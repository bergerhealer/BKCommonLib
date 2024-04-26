package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.AttributeBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.AttributeBase")
public abstract class AttributeBaseHandle extends Template.Handle {
    /** @see AttributeBaseClass */
    public static final AttributeBaseClass T = Template.Class.create(AttributeBaseClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AttributeBaseHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getDescriptionId();
    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.AttributeBase</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeBaseClass extends Template.Class<AttributeBaseHandle> {
        public final Template.Method<String> getDescriptionId = new Template.Method<String>();

    }

}

