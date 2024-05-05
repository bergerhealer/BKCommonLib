package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EnumItemSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.EnumItemSlot")
public abstract class EnumItemSlotHandle extends Template.Handle {
    /** @see EnumItemSlotClass */
    public static final EnumItemSlotClass T = Template.Class.create(EnumItemSlotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EnumItemSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getFilterFlag();
    public abstract String getName();
    public static Object fromFilterFlagRaw(int index) {
        for (Object value : T.getType().getEnumConstants()) {
            if (T.getFilterFlag.invoke(value).intValue() == index) {
                return value;
            }
        }
        return null;
    }
    /**
     * Stores class members for <b>net.minecraft.world.entity.EnumItemSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumItemSlotClass extends Template.Class<EnumItemSlotHandle> {
        public final Template.Method<Integer> getFilterFlag = new Template.Method<Integer>();
        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

