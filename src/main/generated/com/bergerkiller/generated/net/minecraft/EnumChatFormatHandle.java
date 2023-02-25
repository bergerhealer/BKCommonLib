package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.EnumChatFormat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.EnumChatFormat")
public abstract class EnumChatFormatHandle extends Template.Handle {
    /** @See {@link EnumChatFormatClass} */
    public static final EnumChatFormatClass T = Template.Class.create(EnumChatFormatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumChatFormatHandle RESET = T.RESET.getSafe();
    /* ============================================================================== */

    public static EnumChatFormatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();

    public static final Object[] RAW_VALUES = T.getType().getEnumConstants();
    public static final EnumChatFormatHandle[] VALUES;
    static {
        VALUES = new EnumChatFormatHandle[RAW_VALUES.length];
        for (int i = 0; i < VALUES.length; i++) {
            VALUES[i] = createHandle(RAW_VALUES[i]);
        }
    }

    public static EnumChatFormatHandle byChar(char c) {
        for (EnumChatFormatHandle format : VALUES) {
            String s = format.toString();
            if (s.length() >= 2 && s.charAt(1) == c) {
                return format;
            }
        }
        return RESET;
    }

    public static EnumChatFormatHandle byId(int id) {
        if (id >= 0) {
            for (EnumChatFormatHandle format : VALUES) {
                if (format.getId() == id) {
                    return format;
                }
            }
        }
        return RESET;
    }
    /**
     * Stores class members for <b>net.minecraft.EnumChatFormat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumChatFormatClass extends Template.Class<EnumChatFormatHandle> {
        public final Template.EnumConstant.Converted<EnumChatFormatHandle> RESET = new Template.EnumConstant.Converted<EnumChatFormatHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

