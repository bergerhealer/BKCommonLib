package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.PackedItem;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket")
public abstract class ClientboundSetEntityDataPacketHandle extends PacketHandle {
    /** @see ClientboundSetEntityDataPacketClass */
    public static final ClientboundSetEntityDataPacketClass T = Template.Class.create(ClientboundSetEntityDataPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetEntityDataPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSetEntityDataPacketHandle createForSpawn(int entityId, DataWatcher datawatcher) {
        return T.createForSpawn.invoke(entityId, datawatcher);
    }

    public static ClientboundSetEntityDataPacketHandle createForChanges(int entityId, DataWatcher datawatcher) {
        return T.createForChanges.invoke(entityId, datawatcher);
    }

    public static ClientboundSetEntityDataPacketHandle createNew(int entityId, DataWatcher datawatcher, boolean includeUnchangedData) {
        if (includeUnchangedData) {
            return createForSpawn(entityId, datawatcher);
        } else {
            return createForChanges(entityId, datawatcher);
        }
    }

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_METADATA;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract List<PackedItem<Object>> getMetadataItems();
    public abstract void setMetadataItems(List<PackedItem<Object>> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetEntityDataPacketClass extends Template.Class<ClientboundSetEntityDataPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<List<PackedItem<Object>>> metadataItems = new Template.Field.Converted<List<PackedItem<Object>>>();

        public final Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle> createForSpawn = new Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle> createForChanges = new Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle>();

    }

}

