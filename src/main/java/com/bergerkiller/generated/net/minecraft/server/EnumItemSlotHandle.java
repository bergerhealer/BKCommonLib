package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumItemSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EnumItemSlotHandle extends Template.Handle {
    /** @See {@link EnumItemSlotClass} */
    public static final EnumItemSlotClass T = new EnumItemSlotClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumItemSlotHandle.class, "net.minecraft.server.EnumItemSlot", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EnumItemSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getIndex();
    public abstract String getName();

    public static Object fromIndexRaw(int index) {
        for (Object value : T.getType().getEnumConstants()) {
            if (T.getIndex.invoke(value).intValue() == index) {
                return value;
            }
        }
        return null;
    }
    /**
     * Stores class members for <b>net.minecraft.server.EnumItemSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumItemSlotClass extends Template.Class<EnumItemSlotHandle> {
        public final Template.Method<Integer> getIndex = new Template.Method<Integer>();
        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

