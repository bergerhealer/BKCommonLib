package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartFurnace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.EntityMinecartFurnace")
public abstract class EntityMinecartFurnaceHandle extends EntityMinecartAbstractHandle {
    /** @see EntityMinecartFurnaceClass */
    public static final EntityMinecartFurnaceClass T = Template.Class.create(EntityMinecartFurnaceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityMinecartFurnaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Vector getPushForce();
    public abstract void setPushForce(double fx, double fy, double fz);
    public static final Key<Boolean> DATA_SMOKING = Key.Type.BOOLEAN.createKey(T.DATA_SMOKING, 16);

    public void setPushForce(org.bukkit.util.Vector force) {
        setPushForce(force.getX(), force.getY(), force.getZ());
    }

    @Deprecated
    public double getPushForceX() {
        return getPushForce().getX();
    }

    @Deprecated
    public double getPushForceZ() {
        return getPushForce().getZ();
    }

    @Deprecated
    public void setPushForceX(double x) {
        Vector v = getPushForce();
        setPushForce(x, v.getY(), v.getZ());
    }

    @Deprecated
    public void setPushForceZ(double z) {
        Vector v = getPushForce();
        setPushForce(v.getX(), v.getY(), z);
    }
    public abstract int getFuel();
    public abstract void setFuel(int value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartFurnace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartFurnaceClass extends Template.Class<EntityMinecartFurnaceHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_SMOKING = new Template.StaticField.Converted<Key<Boolean>>();

        public final Template.Field.Integer fuel = new Template.Field.Integer();

        public final Template.Method.Converted<Vector> getPushForce = new Template.Method.Converted<Vector>();
        public final Template.Method<Void> setPushForce = new Template.Method<Void>();

    }

}

