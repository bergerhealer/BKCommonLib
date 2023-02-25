package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import java.util.Collection;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ai.attributes.AttributeMapBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ai.attributes.AttributeMapBase")
public abstract class AttributeMapBaseHandle extends Template.Handle {
    /** @see AttributeMapBaseClass */
    public static final AttributeMapBaseClass T = Template.Class.create(AttributeMapBaseClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AttributeMapBaseHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Set<AttributeModifiableHandle> getAttributes();
    public abstract Collection<AttributeModifiableHandle> getSynchronizedAttributes();
    public abstract void loadFromNBT(CommonTagList nbttaglist);
    public abstract CommonTagList saveToNBT();
    /**
     * Stores class members for <b>net.minecraft.world.entity.ai.attributes.AttributeMapBase</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeMapBaseClass extends Template.Class<AttributeMapBaseHandle> {
        public final Template.Method.Converted<Set<AttributeModifiableHandle>> getAttributes = new Template.Method.Converted<Set<AttributeModifiableHandle>>();
        public final Template.Method.Converted<Collection<AttributeModifiableHandle>> getSynchronizedAttributes = new Template.Method.Converted<Collection<AttributeModifiableHandle>>();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();

    }

}

