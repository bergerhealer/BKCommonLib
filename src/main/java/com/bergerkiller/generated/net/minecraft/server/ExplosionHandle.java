package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class ExplosionHandle extends Template.Handle {
    public static final ExplosionClass T = new ExplosionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ExplosionHandle.class, "net.minecraft.server.Explosion");


    /* ============================================================================== */

    public static ExplosionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ExplosionHandle handle = new ExplosionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final ExplosionHandle createNew(World world, Entity entity, double x, double y, double z, float yield, boolean fire, boolean destroyBlocks) {
        return T.constr_world_entity_x_y_z_yield_fire_destroyBlocks.newInstance(world, entity, x, y, z, yield, fire, destroyBlocks);
    }

    /* ============================================================================== */

    public static final class ExplosionClass extends Template.Class<ExplosionHandle> {
        public final Template.Constructor.Converted<ExplosionHandle> constr_world_entity_x_y_z_yield_fire_destroyBlocks = new Template.Constructor.Converted<ExplosionHandle>();

    }
}
