package com.bergerkiller.bukkit.common.conversion.type;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;

import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;

import com.bergerkiller.bukkit.common.conversion.Converter;

/**
 * Deals with the IChatComponent messy-ness in Minecraft, doing translations between:
 * <ul>
 * <li>Text <> JSON
 * <li>IChatComponent <> JSON
 * <li>IChatComponent <> Text
 * </ul>
 */
public abstract class TextFormatConverter<T> extends Converter<T> {
    public static final TextFormatConverter<String> chatComponentToText = new TextFormatConverter<String>(String.class) {
        @Override
        public String convert(Object value, String def) {
            if (value instanceof IChatBaseComponent) {
                //TODO: is this valid too?:
                // return ((IChatBaseComponent) value).getText();
                return CraftChatMessage.fromComponent((IChatBaseComponent) value);
            } else {
                return def;
            }
        }
    };
    public static final TextFormatConverter<Object> textToChatComponent = new TextFormatConverter<Object>(IChatBaseComponent.class) {
        @Override
        public Object convert(Object value, Object def) {
            if (value == null) {
                return def;
            } else {
                IChatBaseComponent[] components = CraftChatMessage.fromString(value.toString());
                return components[0]; //TODO: Wut?
            }
        }
    };

    public static TextFormatConverter<String> chatComponentToJson = new TextFormatConverter<String>(String.class) {
        @Override
        public String convert(Object value, String def) {
            if (value instanceof IChatBaseComponent) {
                return IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) value);
            } else {
                return def;
            }
        }
    };
    public static TextFormatConverter<Object> jsonToChatComponent = new TextFormatConverter<Object>(IChatBaseComponent.class) {
        @Override
        public Object convert(Object value, Object def) {
            if (value == null) {
                return def;
            } else {
                return IChatBaseComponent.ChatSerializer.a(value.toString());
            }
        }
    };

    public static TextFormatConverter<String> jsonToText = new TextFormatConverter<String>(String.class) {
        @Override
        public String convert(Object value, String def) {
            Object component = jsonToChatComponent.convert(value);
            if (component == null) {
                return def;
            } else {
                return chatComponentToText.convert(component, def);
            }
        }
    };
    public static TextFormatConverter<String> textToJson = new TextFormatConverter<String>(String.class) {
        @Override
        public String convert(Object value, String def) {
            Object component = textToChatComponent.convert(value);
            if (component == null) {
                return def;
            } else {
                return chatComponentToJson.convert(component, def);
            }
        }
    };

    protected TextFormatConverter(Class<?> outputType) {
        super(outputType);
    }

    @Override
    public boolean isCastingSupported() {
        return false;
    }

    @Override
    public boolean isRegisterSupported() {
        return false;
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
