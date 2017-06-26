package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityMetadata</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutEntityMetadataHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityMetadataClass} */
    public static final PacketPlayOutEntityMetadataClass T = new PacketPlayOutEntityMetadataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityMetadataHandle.class, "net.minecraft.server.PacketPlayOutEntityMetadata");

    /* ============================================================================== */

    public static PacketPlayOutEntityMetadataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutEntityMetadataHandle handle = new PacketPlayOutEntityMetadataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PacketPlayOutEntityMetadataHandle createNew(int entityId, DataWatcher datawatcher, boolean includeUnchangedData) {
        return T.constr_entityId_datawatcher_includeUnchangedData.newInstance(entityId, datawatcher, includeUnchangedData);
    }

    /* ============================================================================== */

    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public List<Item<?>> getMetadataItems() {
        return T.metadataItems.get(instance);
    }

    public void setMetadataItems(List<Item<?>> value) {
        T.metadataItems.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityMetadata</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityMetadataClass extends Template.Class<PacketPlayOutEntityMetadataHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityMetadataHandle> constr_entityId_datawatcher_includeUnchangedData = new Template.Constructor.Converted<PacketPlayOutEntityMetadataHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<List<Item<?>>> metadataItems = new Template.Field.Converted<List<Item<?>>>();

    }

}

