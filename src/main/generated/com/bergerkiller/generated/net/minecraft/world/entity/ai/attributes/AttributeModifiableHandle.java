package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.AttributeModifiable</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.AttributeModifiable")
public abstract class AttributeModifiableHandle extends Template.Handle {
    /** @see AttributeModifiableClass */
    public static final AttributeModifiableClass T = Template.Class.create(AttributeModifiableClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AttributeModifiableHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setValue(double value);
    public abstract double getValue();
    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.AttributeModifiable</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeModifiableClass extends Template.Class<AttributeModifiableHandle> {
        public final Template.Method<Void> setValue = new Template.Method<Void>();
        public final Template.Method<Double> getValue = new Template.Method<Double>();

    }

}

