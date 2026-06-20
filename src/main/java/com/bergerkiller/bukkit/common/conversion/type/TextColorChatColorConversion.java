package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Since 1.16. Converts TextColor (legacy name: ChatHexColor) to Bukkit ChatColor and back.
 * This exists because on 26.2 Paper removed the helper legacy color field in the TextColor constants.
 * This class is used in the ChatText -> legacy chat message conversion logic.
 */
public class TextColorChatColorConversion {
    private static final Map<Object, ChatColor> textColorToChatColor = new IdentityHashMap<>();
    private static final Map<ChatColor, Object> chatColorToTextColor = new EnumMap<>(ChatColor.class);

    static {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            try {
                Class<?> craftChatMessageClass = CommonUtil.getClass("org.bukkit.craftbukkit.util.CraftChatMessage");
                Class<?> textColorClass = CommonUtil.getClass("net.minecraft.network.chat.TextColor");
                Class<?> chatFormattingClass = CommonUtil.getClass("net.minecraft.ChatFormatting");

                Method getChatColor = craftChatMessageClass.getDeclaredMethod("getColor", chatFormattingClass);

                Method fromLegacyFormat = textColorClass.getDeclaredMethod(
                        CommonBootstrap.evaluateMCVersion(">=", "1.18") ? "fromLegacyFormat" : "a",
                        chatFormattingClass);

                for (Object legacyFormat : chatFormattingClass.getEnumConstants()) {
                    Object textColor = fromLegacyFormat.invoke(null, legacyFormat);
                    if (textColor != null) {
                        ChatColor color = (ChatColor) getChatColor.invoke(null, legacyFormat);
                        textColorToChatColor.put(textColor, color);
                        chatColorToTextColor.put(color, textColor);
                    }
                }
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize TextColor <> ChatColor conversion", t);
            }
        }
    }

    @ConverterMethod(input="net.minecraft.network.chat.TextColor")
    public static ChatColor textColorToChatColor(Object textColor) {
        return textColorToChatColor.get(textColor);
    }

    @ConverterMethod(output="net.minecraft.network.chat.TextColor")
    public static Object chatColorToTextColor(ChatColor color) {
        return chatColorToTextColor.get(color);
    }
}
