package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IBlockData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class IBlockDataHandle extends Template.Handle {
    /** @See {@link IBlockDataClass} */
    public static final IBlockDataClass T = new IBlockDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IBlockDataHandle.class, "net.minecraft.server.IBlockData");

    /* ============================================================================== */

    public static IBlockDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public BlockHandle getBlock() {
        return T.getBlock.invoke(getRaw());
    }

    public Map<Object, Object> getStates() {
        return T.getStates.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.IBlockData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockDataClass extends Template.Class<IBlockDataHandle> {
        public final Template.Method.Converted<BlockHandle> getBlock = new Template.Method.Converted<BlockHandle>();
        public final Template.Method.Converted<Map<Object, Object>> getStates = new Template.Method.Converted<Map<Object, Object>>();

    }

}

