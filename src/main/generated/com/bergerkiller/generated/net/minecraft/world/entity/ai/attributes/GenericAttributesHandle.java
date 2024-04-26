package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.GenericAttributes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.GenericAttributes")
public abstract class GenericAttributesHandle extends Template.Handle {
    /** @see GenericAttributesClass */
    public static final GenericAttributesClass T = Template.Class.create(GenericAttributesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final Holder<AttributeBaseHandle> FOLLOW_RANGE = T.FOLLOW_RANGE.getSafe();
    public static final Holder<AttributeBaseHandle> MOVEMENT_SPEED = T.MOVEMENT_SPEED.getSafe();
    /* ============================================================================== */

    public static GenericAttributesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.GenericAttributes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GenericAttributesClass extends Template.Class<GenericAttributesHandle> {
        public final Template.StaticField.Converted<Holder<AttributeBaseHandle>> FOLLOW_RANGE = new Template.StaticField.Converted<Holder<AttributeBaseHandle>>();
        public final Template.StaticField.Converted<Holder<AttributeBaseHandle>> MOVEMENT_SPEED = new Template.StaticField.Converted<Holder<AttributeBaseHandle>>();

    }

}

