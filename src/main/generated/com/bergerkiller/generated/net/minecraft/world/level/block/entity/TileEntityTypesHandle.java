package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.TileEntityTypes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.TileEntityTypes")
public abstract class TileEntityTypesHandle extends Template.Handle {
    /** @see TileEntityTypesClass */
    public static final TileEntityTypesClass T = Template.Class.create(TileEntityTypesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static TileEntityTypesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getRawByKey(MinecraftKeyHandle key) {
        return T.getRawByKey.invoke(key);
    }

    public static Object getRawById(int id) {
        return T.getRawById.invoker.invoke(null,id);
    }

    public abstract MinecraftKeyHandle getKey();
    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.TileEntityTypes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntityTypesClass extends Template.Class<TileEntityTypesHandle> {
        public final Template.StaticMethod.Converted<Object> getRawByKey = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod<Object> getRawById = new Template.StaticMethod<Object>();

        public final Template.Method.Converted<MinecraftKeyHandle> getKey = new Template.Method.Converted<MinecraftKeyHandle>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

