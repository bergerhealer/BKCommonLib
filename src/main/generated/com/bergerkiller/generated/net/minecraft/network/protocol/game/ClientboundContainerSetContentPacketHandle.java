package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket")
public abstract class ClientboundContainerSetContentPacketHandle extends PacketHandle {
    /** @see ClientboundContainerSetContentPacketClass */
    public static final ClientboundContainerSetContentPacketClass T = Template.Class.create(ClientboundContainerSetContentPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundContainerSetContentPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract List<ItemStack> getItems();
    public abstract void setItems(List<ItemStack> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundContainerSetContentPacketClass extends Template.Class<ClientboundContainerSetContentPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<List<ItemStack>> items = new Template.Field.Converted<List<ItemStack>>();

    }

}

