package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.util.Vector;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutExplosion</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutExplosion")
public abstract class PacketPlayOutExplosionHandle extends PacketHandle {
    /** @See {@link PacketPlayOutExplosionClass} */
    public static final PacketPlayOutExplosionClass T = Template.Class.create(PacketPlayOutExplosionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutExplosionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutExplosionHandle createNew(double x, double y, double z, float power, List<IntVector3> blocks, Vector knockback) {
        return T.constr_x_y_z_power_blocks_knockback.newInstanceVA(x, y, z, power, blocks, knockback);
    }

    /* ============================================================================== */

    public abstract double getX();
    public abstract void setX(double value);
    public abstract double getY();
    public abstract void setY(double value);
    public abstract double getZ();
    public abstract void setZ(double value);
    public abstract float getPower();
    public abstract void setPower(float value);
    public abstract List<IntVector3> getBlocks();
    public abstract void setBlocks(List<IntVector3> value);
    public abstract float getKnockbackX();
    public abstract void setKnockbackX(float value);
    public abstract float getKnockbackY();
    public abstract void setKnockbackY(float value);
    public abstract float getKnockbackZ();
    public abstract void setKnockbackZ(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutExplosion</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutExplosionClass extends Template.Class<PacketPlayOutExplosionHandle> {
        public final Template.Constructor.Converted<PacketPlayOutExplosionHandle> constr_x_y_z_power_blocks_knockback = new Template.Constructor.Converted<PacketPlayOutExplosionHandle>();

        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.Field.Float power = new Template.Field.Float();
        public final Template.Field.Converted<List<IntVector3>> blocks = new Template.Field.Converted<List<IntVector3>>();
        public final Template.Field.Float knockbackX = new Template.Field.Float();
        public final Template.Field.Float knockbackY = new Template.Field.Float();
        public final Template.Field.Float knockbackZ = new Template.Field.Float();

    }

}

