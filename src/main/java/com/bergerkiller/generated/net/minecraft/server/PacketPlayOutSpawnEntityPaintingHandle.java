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
public class PacketPlayOutSpawnEntityPaintingHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSpawnEntityPaintingClass} */
    public static final PacketPlayOutSpawnEntityPaintingClass T = new PacketPlayOutSpawnEntityPaintingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutSpawnEntityPaintingHandle.class, "net.minecraft.server.PacketPlayOutSpawnEntityPainting");

    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityPaintingHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutSpawnEntityPaintingHandle handle = new PacketPlayOutSpawnEntityPaintingHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public IntVector3 getPosition() {
        return T.position.get(instance);
    }

    public void setPosition(IntVector3 value) {
        T.position.set(instance, value);
    }

    public BlockFace getFacing() {
        return T.facing.get(instance);
    }

    public void setFacing(BlockFace value) {
        T.facing.set(instance, value);
    }

    public String getArt() {
        return T.art.get(instance);
    }

    public void setArt(String value) {
        T.art.set(instance, value);
    }

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

