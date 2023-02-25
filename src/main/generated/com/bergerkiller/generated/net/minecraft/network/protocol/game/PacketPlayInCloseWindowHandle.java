package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInCloseWindow</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInCloseWindow")
public abstract class PacketPlayInCloseWindowHandle extends PacketHandle {
    /** @see PacketPlayInCloseWindowClass */
    public static final PacketPlayInCloseWindowClass T = Template.Class.create(PacketPlayInCloseWindowClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInCloseWindowHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInCloseWindow</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInCloseWindowClass extends Template.Class<PacketPlayInCloseWindowHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();

    }

}

