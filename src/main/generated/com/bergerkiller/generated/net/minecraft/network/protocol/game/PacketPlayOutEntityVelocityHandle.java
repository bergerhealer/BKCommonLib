package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity")
public abstract class PacketPlayOutEntityVelocityHandle extends PacketHandle {
    /** @see PacketPlayOutEntityVelocityClass */
    public static final PacketPlayOutEntityVelocityClass T = Template.Class.create(PacketPlayOutEntityVelocityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityVelocityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityVelocityHandle createNew(Entity entity) {
        return T.constr_entity.newInstance(entity);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityVelocityHandle createNew(int entityId, double motX, double motY, double motZ) {
        return T.createNew.invoke(entityId, motX, motY, motZ);
    }

    public abstract Vector getMotVector();
    public abstract void setMotVector(Vector movement);
    public abstract double getMotX();
    public abstract double getMotY();
    public abstract double getMotZ();
    public abstract void setMotX(double x);
    public abstract void setMotY(double y);
    public abstract void setMotZ(double z);
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_VELOCITY;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityVelocityClass extends Template.Class<PacketPlayOutEntityVelocityHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityVelocityHandle> constr_entity = new Template.Constructor.Converted<PacketPlayOutEntityVelocityHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutEntityVelocityHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityVelocityHandle>();

        public final Template.Method.Converted<Vector> getMotVector = new Template.Method.Converted<Vector>();
        public final Template.Method.Converted<Void> setMotVector = new Template.Method.Converted<Void>();
        public final Template.Method<Double> getMotX = new Template.Method<Double>();
        public final Template.Method<Double> getMotY = new Template.Method<Double>();
        public final Template.Method<Double> getMotZ = new Template.Method<Double>();
        public final Template.Method<Void> setMotX = new Template.Method<Void>();
        public final Template.Method<Void> setMotY = new Template.Method<Void>();
        public final Template.Method<Void> setMotZ = new Template.Method<Void>();

    }

}

