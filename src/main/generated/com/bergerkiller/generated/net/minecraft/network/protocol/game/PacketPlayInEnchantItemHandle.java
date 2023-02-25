package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInEnchantItem</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInEnchantItem")
public abstract class PacketPlayInEnchantItemHandle extends PacketHandle {
    /** @see PacketPlayInEnchantItemClass */
    public static final PacketPlayInEnchantItemClass T = Template.Class.create(PacketPlayInEnchantItemClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInEnchantItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract int getButtonId();
    public abstract void setButtonId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInEnchantItem</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInEnchantItemClass extends Template.Class<PacketPlayInEnchantItemHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer buttonId = new Template.Field.Integer();

    }

}

