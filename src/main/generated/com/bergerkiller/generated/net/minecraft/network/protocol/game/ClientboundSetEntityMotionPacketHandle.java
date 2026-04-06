package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket")
public abstract class ClientboundSetEntityMotionPacketHandle extends PacketHandle {
    /** @see ClientboundSetEntityMotionPacketClass */
    public static final ClientboundSetEntityMotionPacketClass T = Template.Class.create(ClientboundSetEntityMotionPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetEntityMotionPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ClientboundSetEntityMotionPacketHandle createNew(Entity entity) {
        return T.constr_entity.newInstance(entity);
    }

    /* ============================================================================== */

    public static ClientboundSetEntityMotionPacketHandle createNew(int entityId, double motX, double motY, double motZ) {
        return T.createNew.invoke(entityId, motX, motY, motZ);
    }

    public abstract Vector getMotVector();
    public abstract double getMotX();
    public abstract double getMotY();
    public abstract double getMotZ();
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_VELOCITY;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetEntityMotionPacketClass extends Template.Class<ClientboundSetEntityMotionPacketHandle> {
        public final Template.Constructor.Converted<ClientboundSetEntityMotionPacketHandle> constr_entity = new Template.Constructor.Converted<ClientboundSetEntityMotionPacketHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<ClientboundSetEntityMotionPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundSetEntityMotionPacketHandle>();

        public final Template.Method.Converted<Vector> getMotVector = new Template.Method.Converted<Vector>();
        public final Template.Method<Double> getMotX = new Template.Method<Double>();
        public final Template.Method<Double> getMotY = new Template.Method<Double>();
        public final Template.Method<Double> getMotZ = new Template.Method<Double>();

    }

}

