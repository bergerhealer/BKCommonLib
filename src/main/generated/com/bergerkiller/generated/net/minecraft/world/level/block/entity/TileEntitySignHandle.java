package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.TileEntitySign</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.TileEntitySign")
public abstract class TileEntitySignHandle extends TileEntityHandle {
    /** @see TileEntitySignClass */
    public static final TileEntitySignClass T = Template.Class.create(TileEntitySignClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static TileEntitySignHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object[] getRawLines();

    @Override
    public org.bukkit.block.Sign toBukkit() {
        return (org.bukkit.block.Sign) super.toBukkit();
    }

    public static TileEntitySignHandle fromBukkit(org.bukkit.block.Sign sign) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion.INSTANCE.blockStateToTileEntity(sign));
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.TileEntitySign</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntitySignClass extends Template.Class<TileEntitySignHandle> {
        public final Template.Method<Object[]> getRawLines = new Template.Method<Object[]>();

    }

}

