package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket")
public abstract class ServerboundSetCreativeModeSlotPacketHandle extends PacketHandle {
    /** @see ServerboundSetCreativeModeSlotPacketClass */
    public static final ServerboundSetCreativeModeSlotPacketClass T = Template.Class.create(ServerboundSetCreativeModeSlotPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundSetCreativeModeSlotPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundSetCreativeModeSlotPacketHandle createNew(int slotIndex, ItemStack item) {
        return T.createNew.invoke(slotIndex, item);
    }

    public abstract ItemStack getItem();
    public abstract int getSlotIndex();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundSetCreativeModeSlotPacketClass extends Template.Class<ServerboundSetCreativeModeSlotPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundSetCreativeModeSlotPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundSetCreativeModeSlotPacketHandle>();

        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted<ItemStack>();
        public final Template.Method<Integer> getSlotIndex = new Template.Method<Integer>();

    }

}

