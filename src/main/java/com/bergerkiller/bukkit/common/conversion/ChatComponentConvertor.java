package com.bergerkiller.bukkit.common.conversion;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import org.bukkit.entity.Player;

public class ChatComponentConvertor {

    public static IChatBaseComponent convertJsonToIChatBaseComponent(String text) {
        return ChatSerializer.a(text);
    }

    public static IChatBaseComponent convertTextToIChatBaseComponent(String text) {
        return ChatSerializer.a(convertTextToJson(text));
    }

    public static String convertIChatBaseComponentToJson(IChatBaseComponent b) {
        return ChatSerializer.a(b);
    }

    public static String convertIChatBaseComponentToText(IChatBaseComponent b) {
        return b.getText();
    }

    public static String convertTextToJson(String text) {
        if (text == null || text.length() == 0) {
            return "\"\"";
        }
        char c;
        int i;
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = text.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static String setPlayerName(Player player, String text) {
        return text.replaceAll("(?i)\\{PLAYER\\}", player.getName());
    }
}
