package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.BlockEntityType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.BlockEntityType")
public abstract class BlockEntityTypeHandle extends Template.Handle {
    /** @see BlockEntityTypeClass */
    public static final BlockEntityTypeClass T = Template.Class.create(BlockEntityTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockEntityTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getRawByKey(IdentifierHandle key) {
        return T.getRawByKey.invoke(key);
    }

    public static Object getRawById(int id) {
        return T.getRawById.invoker.invoke(null,id);
    }

    public abstract IdentifierHandle getKey();
    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.BlockEntityType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockEntityTypeClass extends Template.Class<BlockEntityTypeHandle> {
        public final Template.StaticMethod.Converted<Object> getRawByKey = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod<Object> getRawById = new Template.StaticMethod<Object>();

        public final Template.Method.Converted<IdentifierHandle> getKey = new Template.Method.Converted<IdentifierHandle>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

