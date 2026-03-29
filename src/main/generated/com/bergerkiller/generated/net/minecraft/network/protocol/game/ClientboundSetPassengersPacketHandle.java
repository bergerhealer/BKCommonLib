package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetPassengersPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetPassengersPacket")
public abstract class ClientboundSetPassengersPacketHandle extends PacketHandle {
    /** @see ClientboundSetPassengersPacketClass */
    public static final ClientboundSetPassengersPacketClass T = Template.Class.create(ClientboundSetPassengersPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetPassengersPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSetPassengersPacketHandle createNew() {
        return T.createNew.invoke();
    }

    public void addMountedEntityId(int entityId) {
        int[] oldIds = this.getMountedEntityIds();
        if (oldIds == null || oldIds.length == 0) {
            this.setMountedEntityIds(new int[] {entityId});
        } else {
            int[] newIds = new int[oldIds.length + 1];
            for (int i = 0; i < oldIds.length; i++) {
                newIds[i] = oldIds[i];
            }
            newIds[newIds.length - 1] = entityId;
            this.setMountedEntityIds(newIds);
        }
    }

    public static ClientboundSetPassengersPacketHandle createNew(int entityId, int[] mountedEntityIds) {
        ClientboundSetPassengersPacketHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setMountedEntityIds(mountedEntityIds);
        return handle;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract int[] getMountedEntityIds();
    public abstract void setMountedEntityIds(int[] value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetPassengersPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetPassengersPacketClass extends Template.Class<ClientboundSetPassengersPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field<int[]> mountedEntityIds = new Template.Field<int[]>();

        public final Template.StaticMethod.Converted<ClientboundSetPassengersPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundSetPassengersPacketHandle>();

    }

}

