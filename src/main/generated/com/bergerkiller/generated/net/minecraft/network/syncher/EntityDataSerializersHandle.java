package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.EntityDataSerializers</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.syncher.EntityDataSerializers")
public abstract class EntityDataSerializersHandle extends Template.Handle {
    /** @see EntityDataSerializersClass */
    public static final EntityDataSerializersClass T = Template.Class.create(EntityDataSerializersClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityDataSerializersHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int getSerializerId(Object paramDataWatcherSerializer) {
        return T.getSerializerId.invoke(paramDataWatcherSerializer);
    }

    /**
     * Stores class members for <b>net.minecraft.network.syncher.EntityDataSerializers</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityDataSerializersClass extends Template.Class<EntityDataSerializersHandle> {
        public final Template.StaticMethod.Converted<Integer> getSerializerId = new Template.StaticMethod.Converted<Integer>();

    }

}

