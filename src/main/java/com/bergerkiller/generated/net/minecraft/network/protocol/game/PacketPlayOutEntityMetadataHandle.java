package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata")
public abstract class PacketPlayOutEntityMetadataHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityMetadataClass} */
    public static final PacketPlayOutEntityMetadataClass T = Template.Class.create(PacketPlayOutEntityMetadataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityMetadataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityMetadataHandle createNew(int entityId, DataWatcher datawatcher, boolean includeUnchangedData) {
        return T.constr_entityId_datawatcher_includeUnchangedData.newInstance(entityId, datawatcher, includeUnchangedData);
    }

    /* ============================================================================== */


    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_METADATA;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract List<Item<Object>> getMetadataItems();
    public abstract void setMetadataItems(List<Item<Object>> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityMetadataClass extends Template.Class<PacketPlayOutEntityMetadataHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityMetadataHandle> constr_entityId_datawatcher_includeUnchangedData = new Template.Constructor.Converted<PacketPlayOutEntityMetadataHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<List<Item<Object>>> metadataItems = new Template.Field.Converted<List<Item<Object>>>();

    }

}

