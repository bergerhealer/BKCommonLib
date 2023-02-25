package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.DataWatcherRegistry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.syncher.DataWatcherRegistry")
public abstract class DataWatcherRegistryHandle extends Template.Handle {
    /** @see DataWatcherRegistryClass */
    public static final DataWatcherRegistryClass T = Template.Class.create(DataWatcherRegistryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DataWatcherRegistryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int getSerializerId(Object paramDataWatcherSerializer) {
        return T.getSerializerId.invoke(paramDataWatcherSerializer);
    }

    /**
     * Stores class members for <b>net.minecraft.network.syncher.DataWatcherRegistry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherRegistryClass extends Template.Class<DataWatcherRegistryHandle> {
        public final Template.StaticMethod.Converted<Integer> getSerializerId = new Template.StaticMethod.Converted<Integer>();

    }

}

