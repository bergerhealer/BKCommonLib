package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket")
public abstract class ClientboundContainerSetSlotPacketHandle extends PacketHandle {
    /** @see ClientboundContainerSetSlotPacketClass */
    public static final ClientboundContainerSetSlotPacketClass T = Template.Class.create(ClientboundContainerSetSlotPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundContainerSetSlotPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundContainerSetSlotPacketHandle createNew(int containerId, int slot, ItemStack item) {
        return T.createNew.invoke(containerId, slot, item);
    }

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract int getSlot();
    public abstract void setSlot(int value);
    public abstract ItemStack getItem();
    public abstract void setItem(ItemStack value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundContainerSetSlotPacketClass extends Template.Class<ClientboundContainerSetSlotPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer slot = new Template.Field.Integer();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted<ItemStack>();

        public final Template.StaticMethod.Converted<ClientboundContainerSetSlotPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundContainerSetSlotPacketHandle>();

    }

}

