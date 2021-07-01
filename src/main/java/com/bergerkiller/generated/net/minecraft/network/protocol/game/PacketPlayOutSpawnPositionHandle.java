package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition")
public abstract class PacketPlayOutSpawnPositionHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSpawnPositionClass} */
    public static final PacketPlayOutSpawnPositionClass T = Template.Class.create(PacketPlayOutSpawnPositionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setAngle(float angle);
    public abstract float getAngle();
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnPositionClass extends Template.Class<PacketPlayOutSpawnPositionHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();

        public final Template.Method<Void> setAngle = new Template.Method<Void>();
        public final Template.Method<Float> getAngle = new Template.Method<Float>();

    }

}

