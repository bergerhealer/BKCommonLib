package com.bergerkiller.generated.org.bukkit.craftbukkit.block.data;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.data.CraftBlockData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("org.bukkit.craftbukkit.block.data.CraftBlockData")
public abstract class CraftBlockDataHandle extends Template.Handle {
    /** @See {@link CraftBlockDataClass} */
    public static final CraftBlockDataClass T = Template.Class.create(CraftBlockDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftBlockDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object fromData(BlockData data) {
        return T.fromData.invoke(data);
    }

    public abstract BlockData getState();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.data.CraftBlockData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockDataClass extends Template.Class<CraftBlockDataHandle> {
        public final Template.StaticMethod.Converted<Object> fromData = new Template.StaticMethod.Converted<Object>();

        public final Template.Method.Converted<BlockData> getState = new Template.Method.Converted<BlockData>();

    }

}

