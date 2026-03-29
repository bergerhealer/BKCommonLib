package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.ChatFormatting</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.ChatFormatting")
public abstract class ChatFormattingHandle extends Template.Handle {
    /** @see ChatFormattingClass */
    public static final ChatFormattingClass T = Template.Class.create(ChatFormattingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final ChatFormattingHandle RESET = T.RESET.getSafe();
    /* ============================================================================== */

    public static ChatFormattingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();
    public static final Object[] RAW_VALUES = T.getType().getEnumConstants();
    public static final ChatFormattingHandle[] VALUES;
    static {
        VALUES = new ChatFormattingHandle[RAW_VALUES.length];
        for (int i = 0; i < VALUES.length; i++) {
            VALUES[i] = createHandle(RAW_VALUES[i]);
        }
    }

    public static ChatFormattingHandle byChar(char c) {
        for (ChatFormattingHandle format : VALUES) {
            String s = format.toString();
            if (s.length() >= 2 && s.charAt(1) == c) {
                return format;
            }
        }
        return RESET;
    }

    public static ChatFormattingHandle byId(int id) {
        if (id >= 0) {
            for (ChatFormattingHandle format : VALUES) {
                if (format.getId() == id) {
                    return format;
                }
            }
        }
        return RESET;
    }
    /**
     * Stores class members for <b>net.minecraft.ChatFormatting</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChatFormattingClass extends Template.Class<ChatFormattingHandle> {
        public final Template.EnumConstant.Converted<ChatFormattingHandle> RESET = new Template.EnumConstant.Converted<ChatFormattingHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

