package com.bergerkiller.bukkit.common.conversion.type;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.inventory.EquipmentSlot;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.generated.net.minecraft.world.entity.EnumItemSlotHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * Converts between EnumItemSlot and Bukkit EquipmentSlot.
 * Only works on a later version of Minecraft 1.8, where EnumItemSlot exists.
 */
public class ItemSlotConversion {
    private static final Map<Object, EquipmentSlot> slotMap_a = new IdentityHashMap<Object, EquipmentSlot>();
    private static final Map<EquipmentSlot, Object> slotMap_b = new IdentityHashMap<EquipmentSlot, Object>();

    static {
        Object[] enumItemSlotValues = EnumItemSlotHandle.T.getType().getEnumConstants();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            String name = slot.name().toLowerCase(Locale.ENGLISH).replace("_", "");
            if (name.equals("hand")) {
                name = "mainhand";
            }

            Object foundItemSlot = null;
            for (Object enumItemSlot : enumItemSlotValues) {
                if (EnumItemSlotHandle.T.getName.invoke(enumItemSlot).equals(name)) {
                    foundItemSlot = enumItemSlot;
                    break;
                }
            }
            if (foundItemSlot != null) {
                // Exact name match. Put it in A>B and B>A
                slotMap_a.put(foundItemSlot, slot);
                slotMap_b.put(slot, foundItemSlot);
                continue;
            }

            // Unable to find
            Logging.LOGGER_REFLECTION.warning("Failed to find matching EnumItemSlot for EquipmentSlot " + name);
        }

        for (Object enumItemSlot : enumItemSlotValues) {
            if (slotMap_a.containsKey(enumItemSlot)) {
                continue;
            }

            // Unable to find
            String name = EnumItemSlotHandle.T.getName.invoke(enumItemSlot);
            Logging.LOGGER_REFLECTION.warning("Failed to find matching EquipmentSlot for EnumItemSlot " + name);
        }
    }

    @ConverterMethod(output="net.minecraft.world.entity.EnumItemSlot")
    public static Object getEnumItemSlot(EquipmentSlot slot) {
        return slotMap_b.get(slot);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EnumItemSlot")
    public static EquipmentSlot getEquipmentSlot(Object enumItemSlot) {
        return slotMap_a.get(enumItemSlot);
    }

    @ConverterMethod
    public static int equipmentSlotToFilterFlag(EquipmentSlot equipmentSlot) {
        return WrapperConversion.enumItemSlotToFilterFlag(getEnumItemSlot(equipmentSlot));
    }
}
