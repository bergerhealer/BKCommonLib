package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumItemSlotHandle extends Template.Handle {
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

    public org.bukkit.inventory.EquipmentSlot toBukkit() {
        return org.bukkit.inventory.EquipmentSlot.values()[((Enum<?>) instance).ordinal()];
    }

    public static Object fromBukkitRaw(org.bukkit.inventory.EquipmentSlot slot) {
        return T.getType().getEnumConstants()[slot.ordinal()];
    }

    public static EnumItemSlotHandle fromBukkit(org.bukkit.inventory.EquipmentSlot slot) {
        return createHandle(T.getType().getEnumConstants()[slot.ordinal()]);
    }

    public static final class EnumItemSlotClass extends Template.Class<EnumItemSlotHandle> {
    }
}
