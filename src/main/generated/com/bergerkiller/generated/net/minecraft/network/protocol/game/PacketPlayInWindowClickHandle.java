package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInWindowClick</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInWindowClick")
public abstract class PacketPlayInWindowClickHandle extends PacketHandle {
    /** @see PacketPlayInWindowClickClass */
    public static final PacketPlayInWindowClickClass T = Template.Class.create(PacketPlayInWindowClickClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInWindowClickHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract short getSlot();
    public abstract void setSlot(short value);
    public abstract byte getButton();
    public abstract void setButton(byte value);
    public abstract InventoryClickType getMode();
    public abstract void setMode(InventoryClickType value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInWindowClick</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInWindowClickClass extends Template.Class<PacketPlayInWindowClickHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<Short> slot = new Template.Field.Converted<Short>();
        public final Template.Field.Converted<Byte> button = new Template.Field.Converted<Byte>();
        public final Template.Field.Converted<InventoryClickType> mode = new Template.Field.Converted<InventoryClickType>();

    }

}

