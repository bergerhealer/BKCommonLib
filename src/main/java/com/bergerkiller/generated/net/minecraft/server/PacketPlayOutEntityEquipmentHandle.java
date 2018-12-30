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
public abstract class PacketPlayOutEntityEquipmentHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityEquipmentClass} */
    public static final PacketPlayOutEntityEquipmentClass T = new PacketPlayOutEntityEquipmentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityEquipmentHandle.class, "net.minecraft.server.PacketPlayOutEntityEquipment", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutEntityEquipmentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityEquipmentHandle createNew(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return T.constr_entityId_slot_itemStack.newInstance(entityId, slot, itemStack);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract EquipmentSlot getSlot();
    public abstract void setSlot(EquipmentSlot value);
    public abstract ItemStack getItemStack();
    public abstract void setItemStack(ItemStack value);
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

