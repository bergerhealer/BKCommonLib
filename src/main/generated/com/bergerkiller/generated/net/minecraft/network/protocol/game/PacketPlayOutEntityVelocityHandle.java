package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity")
public abstract class PacketPlayOutEntityVelocityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityVelocityClass} */
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


    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_VELOCITY;
    }

    public double getMotX() {
        return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializeVelocity(T.motX_raw.getInteger(getRaw()));
    }

    public double getMotY() {
        return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializeVelocity(T.motY_raw.getInteger(getRaw()));
    }

    public double getMotZ() {
        return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializeVelocity(T.motZ_raw.getInteger(getRaw()));
    }

    public void setMotX(double motX) {
        T.motX_raw.setInteger(getRaw(), com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializeVelocity(motX));
    }

    public void setMotY(double motY) {
        T.motY_raw.setInteger(getRaw(), com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializeVelocity(motY));
    }

    public void setMotZ(double motZ) {
        T.motZ_raw.setInteger(getRaw(), com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializeVelocity(motZ));
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract int getMotX_raw();
    public abstract void setMotX_raw(int value);
    public abstract int getMotY_raw();
    public abstract void setMotY_raw(int value);
    public abstract int getMotZ_raw();
    public abstract void setMotZ_raw(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityVelocityClass extends Template.Class<PacketPlayOutEntityVelocityHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityVelocityHandle> constr_entity = new Template.Constructor.Converted<PacketPlayOutEntityVelocityHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Integer motX_raw = new Template.Field.Integer();
        public final Template.Field.Integer motY_raw = new Template.Field.Integer();
        public final Template.Field.Integer motZ_raw = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutEntityVelocityHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityVelocityHandle>();

    }

}

