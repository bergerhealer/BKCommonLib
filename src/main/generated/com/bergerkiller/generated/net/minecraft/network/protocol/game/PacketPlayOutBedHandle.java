package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutBed</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutBed")
public abstract class PacketPlayOutBedHandle extends PacketHandle {
    /** @see PacketPlayOutBedClass */
    public static final PacketPlayOutBedClass T = Template.Class.create(PacketPlayOutBedClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutBedHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract IntVector3 getBedPosition();
    public abstract void setBedPosition(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutBed</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutBedClass extends Template.Class<PacketPlayOutBedHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> bedPosition = new Template.Field.Converted<IntVector3>();

    }

}

