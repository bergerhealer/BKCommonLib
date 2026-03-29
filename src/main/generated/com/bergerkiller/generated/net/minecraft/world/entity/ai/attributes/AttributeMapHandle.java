package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import java.util.Collection;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.AttributeMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.AttributeMap")
public abstract class AttributeMapHandle extends Template.Handle {
    /** @see AttributeMapClass */
    public static final AttributeMapClass T = Template.Class.create(AttributeMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AttributeMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Collection<AttributeInstanceHandle> getAllAttributes();
    public abstract Set<AttributeInstanceHandle> getChangedSynchronizedAttributes();
    public abstract Collection<AttributeInstanceHandle> getSynchronizedAttributes();
    public abstract void loadFromNBT(CommonTagList nbttaglist);
    public abstract CommonTagList saveToNBT();
    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.AttributeMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeMapClass extends Template.Class<AttributeMapHandle> {
        public final Template.Method.Converted<Collection<AttributeInstanceHandle>> getAllAttributes = new Template.Method.Converted<Collection<AttributeInstanceHandle>>();
        public final Template.Method.Converted<Set<AttributeInstanceHandle>> getChangedSynchronizedAttributes = new Template.Method.Converted<Set<AttributeInstanceHandle>>();
        public final Template.Method.Converted<Collection<AttributeInstanceHandle>> getSynchronizedAttributes = new Template.Method.Converted<Collection<AttributeInstanceHandle>>();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();

    }

}

