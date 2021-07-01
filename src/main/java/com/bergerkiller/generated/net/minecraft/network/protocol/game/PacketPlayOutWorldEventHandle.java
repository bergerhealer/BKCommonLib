package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutWorldEvent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutWorldEvent")
public abstract class PacketPlayOutWorldEventHandle extends PacketHandle {
    /** @See {@link PacketPlayOutWorldEventClass} */
    public static final PacketPlayOutWorldEventClass T = Template.Class.create(PacketPlayOutWorldEventClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutWorldEventHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEffectId();
    public abstract void setEffectId(int value);
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract int getData();
    public abstract void setData(int value);
    public abstract boolean isGlobalEvent();
    public abstract void setGlobalEvent(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutWorldEvent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWorldEventClass extends Template.Class<PacketPlayOutWorldEventHandle> {
        public final Template.Field.Integer effectId = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Integer data = new Template.Field.Integer();
        public final Template.Field.Boolean globalEvent = new Template.Field.Boolean();

    }

}

