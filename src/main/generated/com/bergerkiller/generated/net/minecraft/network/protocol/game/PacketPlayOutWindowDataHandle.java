package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutWindowData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutWindowData")
public abstract class PacketPlayOutWindowDataHandle extends PacketHandle {
    /** @see PacketPlayOutWindowDataClass */
    public static final PacketPlayOutWindowDataClass T = Template.Class.create(PacketPlayOutWindowDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutWindowDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract int getId();
    public abstract void setId(int value);
    public abstract int getValue();
    public abstract void setValue(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutWindowData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWindowDataClass extends Template.Class<PacketPlayOutWindowDataHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer id = new Template.Field.Integer();
        public final Template.Field.Integer value = new Template.Field.Integer();

    }

}

