package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;

import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;

/**
 * Minecraft formatted text represented as chat components, which can be converted between legacy chat messages
 * and JSON formatted text.
 */
public final class ChatText extends BasicWrapper {

    private ChatText() {
    }

    /**
     * Gets the chat text as a JSON-formatted String
     * 
     * @return json chat text
     */
    public final String getJson() {
        if (handle == null) {
            return "{}";
        } else {
            return IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) handle);
        }
    }

    /**
     * Sets the chat text using a JSON-formatted String
     * 
     * @param jsonText to set to
     */
    public final void setJson(String jsonText) {
        handle = IChatBaseComponent.ChatSerializer.a(jsonText);
    }

    /**
     * Gets the chat text as a legacy format-encoded String
     * 
     * @return chat message
     */
    public final String getMessage() {
        if (handle == null) {
            return "";
        } else {
            return CraftChatMessage.fromComponent((IChatBaseComponent) handle);
        }
    }

    /**
     * Sets the chat text using a legacy format-encoded String
     * 
     * @param messageText to set to
     */
    public final void setMessage(String messageText) {
        handle = CraftChatMessage.fromString(messageText)[0];
    }

    public static ChatText fromJson(String jsonText) {
        ChatText text = new ChatText();
        text.setJson(jsonText);
        return text;
    }

    public static ChatText fromMessage(String message) {
        ChatText text = new ChatText();
        text.setMessage(message);
        return text;
    }

    public static ChatText fromComponent(Object iChatBaseComponentHandle) {
        ChatText text = new ChatText();
        text.setHandle(iChatBaseComponentHandle);
        return text;
    }

    /*
     * This was an old function used to convert text to JSON
     * Does this support all chat styling flags CraftBukkit uses?
     * If so, it would be better to use this function over the current conversion method.
     * Saves creating an IChatBaseComponent in between.
     * TODO: Test this! And test performance!
     */
    public static String convertTextToJson_old(String text) {
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
}
