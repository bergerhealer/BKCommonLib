package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumItemSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EnumItemSlotHandle extends Template.Handle {
    /** @See {@link EnumItemSlotClass} */
    public static final EnumItemSlotClass T = new EnumItemSlotClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumItemSlotHandle.class, "net.minecraft.server.EnumItemSlot");

    /* ============================================================================== */

    public static EnumItemSlotHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumItemSlotHandle handle = new EnumItemSlotHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getIndex() {
        return T.getIndex.invoke(instance);
    }


    public static Object fromIndexRaw(int index) {
        for (Object value : T.getType().getEnumConstants()) {
            if (T.getIndex.invoke(value).intValue() == index) {
                return value;
            }
        }
        return null;
    }

    public org.bukkit.inventory.EquipmentSlot toBukkit() {
        return org.bukkit.inventory.EquipmentSlot.values()[((Enum<?>) instance).ordinal()];
    }

    public static Object fromBukkitRaw(org.bukkit.inventory.EquipmentSlot slot) {
        return T.getType().getEnumConstants()[slot.ordinal()];
    }

    public static EnumItemSlotHandle fromBukkit(org.bukkit.inventory.EquipmentSlot slot) {
        return createHandle(T.getType().getEnumConstants()[slot.ordinal()]);
    }
    /**
     * Stores class members for <b>net.minecraft.server.EnumItemSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumItemSlotClass extends Template.Class<EnumItemSlotHandle> {
        public final Template.Method<Integer> getIndex = new Template.Method<Integer>();

    }

}

