package com.bergerkiller.bukkit.common.internal.templates;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class EntityHandle extends Template.Handle {
    public static final EntityTemplate T = new EntityTemplate();

    public final double locX_get() {
        return T.locX.getDouble(instance);
    }

    public final void locX_set(double value) {
        T.locX.setDouble(instance, value);
    }

    public static class EntityTemplate extends Template.Class {
        private EntityTemplate() {
            init(EntityTemplate.class, "net.minecraft.server.Entity");
        }

        public final Template.Field.Double locX = new Template.Field.Double();
        public final Template.Field.Double locY = new Template.Field.Double();
        public final Template.Field.Double locZ = new Template.Field.Double();
    }
}
