package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Art;
import org.bukkit.block.BlockFace;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting")
public abstract class PacketPlayOutSpawnEntityPaintingHandle extends PacketHandle {
    /** @see PacketPlayOutSpawnEntityPaintingClass */
    public static final PacketPlayOutSpawnEntityPaintingClass T = Template.Class.create(PacketPlayOutSpawnEntityPaintingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityPaintingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean hasArtField() {
        return T.hasArtField.invoker.invoke(null);
    }

    public abstract Art getArt();
    public abstract void setArt(Art art);
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 position);
    public abstract BlockFace getFacing();
    public abstract void setFacing(BlockFace facing);
    public abstract void setEntityUUID(UUID uuid);
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityPaintingClass extends Template.Class<PacketPlayOutSpawnEntityPaintingHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod<Boolean> hasArtField = new Template.StaticMethod<Boolean>();

        public final Template.Method.Converted<Art> getArt = new Template.Method.Converted<Art>();
        public final Template.Method.Converted<Void> setArt = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<IntVector3> getPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<Void> setPosition = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<BlockFace> getFacing = new Template.Method.Converted<BlockFace>();
        public final Template.Method.Converted<Void> setFacing = new Template.Method.Converted<Void>();
        public final Template.Method<Void> setEntityUUID = new Template.Method<Void>();

    }

}

