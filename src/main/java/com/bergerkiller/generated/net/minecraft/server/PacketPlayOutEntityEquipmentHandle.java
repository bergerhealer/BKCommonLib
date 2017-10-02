package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityEquipment</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutEntityEquipmentHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityEquipmentClass} */
    public static final PacketPlayOutEntityEquipmentClass T = new PacketPlayOutEntityEquipmentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityEquipmentHandle.class, "net.minecraft.server.PacketPlayOutEntityEquipment");

    /* ============================================================================== */

    public static PacketPlayOutEntityEquipmentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityEquipmentHandle createNew(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return T.constr_entityId_slot_itemStack.newInstance(entityId, slot, itemStack);
    }

    /* ============================================================================== */

    public int getEntityId() {
        return T.entityId.getInteger(getRaw());
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(getRaw(), value);
    }

    public EquipmentSlot getSlot() {
        return T.slot.get(getRaw());
    }

    public void setSlot(EquipmentSlot value) {
        T.slot.set(getRaw(), value);
    }

    public ItemStack getItemStack() {
        return T.itemStack.get(getRaw());
    }

    public void setItemStack(ItemStack value) {
        T.itemStack.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityEquipment</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityEquipmentClass extends Template.Class<PacketPlayOutEntityEquipmentHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityEquipmentHandle> constr_entityId_slot_itemStack = new Template.Constructor.Converted<PacketPlayOutEntityEquipmentHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<EquipmentSlot> slot = new Template.Field.Converted<EquipmentSlot>();
        public final Template.Field.Converted<ItemStack> itemStack = new Template.Field.Converted<ItemStack>();

    }

}

