package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityEquipment</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutEntityEquipment")
public abstract class PacketPlayOutEntityEquipmentHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityEquipmentClass} */
    public static final PacketPlayOutEntityEquipmentClass T = Template.Class.create(PacketPlayOutEntityEquipmentClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityEquipmentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityEquipmentHandle createNew(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return T.createNew.invoke(entityId, slot, itemStack);
    }

    public abstract int getSlotCount();
    public abstract EquipmentSlot getEquipmentSlot(int index);
    public abstract void setEquipmentSlot(int index, EquipmentSlot slot);
    public abstract ItemStack getItemStack(int index);
    public abstract void setItemStack(int index, ItemStack itemStack);
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityEquipment</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityEquipmentClass extends Template.Class<PacketPlayOutEntityEquipmentHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutEntityEquipmentHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityEquipmentHandle>();

        public final Template.Method<Integer> getSlotCount = new Template.Method<Integer>();
        public final Template.Method.Converted<EquipmentSlot> getEquipmentSlot = new Template.Method.Converted<EquipmentSlot>();
        public final Template.Method.Converted<Void> setEquipmentSlot = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ItemStack> getItemStack = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<Void> setItemStack = new Template.Method.Converted<Void>();

    }

}

