package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.block.BlockFace;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutSpawnEntityPainting</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutSpawnEntityPaintingHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSpawnEntityPaintingClass} */
    public static final PacketPlayOutSpawnEntityPaintingClass T = new PacketPlayOutSpawnEntityPaintingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutSpawnEntityPaintingHandle.class, "net.minecraft.server.PacketPlayOutSpawnEntityPainting");

    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityPaintingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public void setEntityUUID(UUID uuid) {
        if (T.entityUUID.isAvailable()) {
            T.entityUUID.set(getRaw(), uuid);
        }
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockFace getFacing();
    public abstract void setFacing(BlockFace value);
    public abstract String getArt();
    public abstract void setArt(String value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutSpawnEntityPainting</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityPaintingClass extends Template.Class<PacketPlayOutSpawnEntityPaintingHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockFace> facing = new Template.Field.Converted<BlockFace>();
        public final Template.Field<String> art = new Template.Field<String>();

    }

}

