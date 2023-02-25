package com.bergerkiller.generated.net.minecraft.world.level.block.state.properties;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.state.properties.IBlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.state.properties.IBlockState")
public abstract class IBlockStateHandle extends Template.Handle {
    /** @See {@link IBlockStateClass} */
    public static final IBlockStateClass T = Template.Class.create(IBlockStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IBlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getKeyToken();
    public abstract String getValueToken(Comparable value);
    public abstract Collection getValues();
    /**
     * Stores class members for <b>net.minecraft.world.level.block.state.properties.IBlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockStateClass extends Template.Class<IBlockStateHandle> {
        public final Template.Method<String> getKeyToken = new Template.Method<String>();
        public final Template.Method<String> getValueToken = new Template.Method<String>();
        public final Template.Method<Collection> getValues = new Template.Method<Collection>();

    }

}

