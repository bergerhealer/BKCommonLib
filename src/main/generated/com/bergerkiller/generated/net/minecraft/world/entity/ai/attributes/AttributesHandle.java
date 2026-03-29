package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.Attributes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.Attributes")
public abstract class AttributesHandle extends Template.Handle {
    /** @see AttributesClass */
    public static final AttributesClass T = Template.Class.create(AttributesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final Holder<AttributeHandle> FOLLOW_RANGE = T.FOLLOW_RANGE.getSafe();
    public static final Holder<AttributeHandle> MOVEMENT_SPEED = T.MOVEMENT_SPEED.getSafe();
    /* ============================================================================== */

    public static AttributesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.Attributes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributesClass extends Template.Class<AttributesHandle> {
        public final Template.StaticField.Converted<Holder<AttributeHandle>> FOLLOW_RANGE = new Template.StaticField.Converted<Holder<AttributeHandle>>();
        public final Template.StaticField.Converted<Holder<AttributeHandle>> MOVEMENT_SPEED = new Template.StaticField.Converted<Holder<AttributeHandle>>();

    }

}

