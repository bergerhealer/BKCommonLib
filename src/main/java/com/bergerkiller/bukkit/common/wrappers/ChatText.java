package com.bergerkiller.bukkit.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;

import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.network.chat.IChatBaseComponentHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftChatMessageHandle;

/**
 * Minecraft formatted text represented as chat components, which can be converted between legacy chat messages
 * and JSON formatted text.
 */
public final class ChatText extends BasicWrapper<IChatBaseComponentHandle> implements Cloneable {

    private ChatText() {
    }

    /**
     * Sends this chat text as a message to a player.
     * 
     * @param player The player to send to
     * @see PlayerUtil#sendMessage(player, message)
     */
    public final void sendTo(Player player) {
        PlayerUtil.sendMessage(player, this);
    }

    /**
     * Sends this chat text as a message to a command sender.
     * If the sender supports special chat styling, then
     * the style is preserved when sending. Otherwise, the
     * {@link #getMessage()} is used.
     *
     * @param sender Recipient
     */
    public final void sendTo(CommandSender sender) {
        if (sender instanceof Player) {
            PlayerUtil.sendMessage((Player) sender, this);
        } else {
            sender.sendMessage(this.getMessage());
        }
    }

    /**
     * Encodes this chat text as NBT suitable for storing as metadata.
     * On version 1.21.5 and later of the server this encodes the full component
     * hierarchy. On versions before that, encodes this component as a json string
     * and returns that.
     *
     * @return Encoded NBT
     */
    public final CommonTag getNBT() {
        if (handle == null) {
            return CommonTagCompound.EMPTY;
        } else {
            return IChatBaseComponentHandle.ChatSerializerHandle.chatComponentToNBT(handle);
        }
    }

    /**
     * Decodes NBT into chat text and assigns it to this ChatText. On version 1.21.5
     * and later this supports the new nbt encoding, on versions older than that it
     * only supports NBTTagString and decoding from json.
     *
     * @param nbt NBT to decode
     */
    public final void setNBT(CommonTag nbt) {
        handle = IChatBaseComponentHandle.ChatSerializerHandle.nbtToChatComponent(nbt);
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
            return IChatBaseComponentHandle.ChatSerializerHandle.chatComponentToJson(handle);
        }
    }

    /**
     * Sets the chat text using a JSON-formatted String
     * 
     * @param jsonText to set to
     */
    public final void setJson(String jsonText) {
        handle = IChatBaseComponentHandle.ChatSerializerHandle.jsonToChatComponent(jsonText);
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
            return CraftChatMessageHandle.fromComponent(handle);
        }
    }

    /**
     * Gets whether this chat text is empty. This method has the benefit of not
     * having to decode the text to figure this out. Do note that some specially formatted
     * messages, like translation or styles, could cause this to return false
     * despite {@link #getMessage()} returning an empty String.
     *
     * @return True if this message is empty
     */
    public final boolean isEmpty() {
        return handle == null || handle.isEmpty();
    }

    /**
     * Checks whether all the ChatText elements of an array are {@link #isEmpty() empty}.
     * Null entries are considered empty.
     *
     * @param lines Lines
     * @return True if all lines are empty
     */
    public static boolean isAllEmpty(ChatText[] lines) {
        for (ChatText line : lines) {
            if (line != null && !line.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the chat text using a legacy format-encoded String
     * 
     * @param messageText to set to
     */
    public final void setMessage(String messageText) {
        // Optimization for empty strings
        if (messageText.isEmpty()) {
            handle = IChatBaseComponentHandle.ChatSerializerHandle.empty();
            return;
        }

        // Use CraftBukkit util's method of parsing
        IChatBaseComponentHandle[] parts = CraftChatMessageHandle.fromString(messageText, true);
        handle = parts[0];
        for (int i = 1; i < parts.length; i++) {
            handle = handle.addSibling(parts[i]);
        }

        // Find trailing color formatting characters in the message that would otherwise be dropped
        ArrayList<ChatColor> trailing_formatting_chars = new ArrayList<ChatColor>(0);
        for (int i = messageText.length() - 2; i >= 0; i -= 2) {
            if (messageText.charAt(i) == StringUtil.CHAT_STYLE_CHAR) {
                ChatColor color = ChatColor.getByChar(messageText.charAt(i + 1));
                if (color != null) {
                    trailing_formatting_chars.add(0, color);
                    continue;
                }
            }
            break;
        }
        if (!trailing_formatting_chars.isEmpty()) {
            handle = handle.addSibling(IChatBaseComponentHandle.ChatSerializerHandle.modifiersToComponent(trailing_formatting_chars));
        }
    }

    /**
     * Appends text to this Chat Text
     * 
     * @param text to append
     * @return this
     */
    public final ChatText append(ChatText text) {
        return append(text.handle);
    }

    private final ChatText append(IChatBaseComponentHandle handle) {
        this.handle = this.handle.addSibling(handle);
        return this;
    }

    /**
     * Appends text to this Chat Text
     * 
     * @param text to append
     * @return this
     */
    public final ChatText append(String text) {
        return append(fromMessage(text));
    }

    /**
     * Appends a  text component that represents a clickable link.
     * When clicked, the player is asked to navigate to the URL specified.
     * 
     * @param text The text visible to the player
     * @param url The url navigated to when clicked
     * @return this
     */
    public final ChatText appendClickableURL(String text, String url) {
        return append(fromClickableURL(text, url));
    }

    /**
     * Appends a  text component that represents a clickable link.
     * When clicked, the player is asked to navigate to the URL specified.
     * 
     * @param text The text visible to the player
     * @param url The url navigated to when clicked
     * @param altText The text displayed to the player when hovering with the cursor on the link
     * @return this
     */
    public final ChatText appendClickableURL(String text, String url, String altText) {
        return append(fromClickableURL(text, url).setHoverText(altText));
    }

    /**
     * Appends a text component that represents a clickable link.
     * When clicked, the player is asked to copy the content specified
     * to their clipboard.
     * 
     * @param text The text visible to the player
     * @param content The content copied
     * @return this
     */
    public final ChatText appendClickableContent(String text, String content) {
        return append(fromClickableContent(text, content));
    }

    /**
     * Appends a text component that inserts a newline character at the end.
     * Further text appended will be on a new line.
     *
     * @return this
     */
    public final ChatText appendNewLine() {
        return append(IChatBaseComponentHandle.ChatSerializerHandle.newLine());
    }

    /**
     * Makes this current chat text message clickable. When a player clicks on the text,
     * it will prompt the player to navigate their Webbrowser to the URL given.
     * 
     * @param url The url to navigate to when clicked
     * @return this
     */
    public final ChatText setClickableURL(String url) {
        handle = handle.setClickableURL(url);
        return this;
    }

    /**
     * Makes this current chat text message clickable. When a player clicks on the text,
     * it will prompt to copy the content specified here to their clipboard. Avoid using
     * too large contents, as they may lag out the client.
     * 
     * @param content The content the player can copy to their clipboard
     * @return this
     */
    public final ChatText setClickableContent(String content) {
        handle = handle.setClickableContent(content);
        return this;
    }

    /**
     * Makes this current chat text message clickable. When a player clicks on the text,
     * it will place the command specified in the chat input box.
     *
     * @param command The command to suggest
     * @return this
     */
    public final ChatText setClickableSuggestedCommand(String command) {
        handle = handle.setClickableSuggestedCommand(command);
        return this;
    }

    /**
     * Makes this current chat text message clickable. When a player clicks on the text,
     * it will execute the command specified.
     *
     * @param command The command to execute
     * @return this
     */
    public final ChatText setClickableRunCommand(String command) {
        handle = handle.setClickableRunCommand(command);
        return this;
    }

    /**
     * Sets the text displayed to the player when he hovers his cursor on top of it
     * 
     * @param hoverText The text displayed
     * @return this
     */
    public final ChatText setHoverText(ChatText hoverText) {
        if (hoverText != null) {
            handle = handle.setHoverText(hoverText.getBackingHandle());
        }
        return this;
    }

    /**
     * Sets the text displayed to the player when he hovers his cursor on top of it
     * 
     * @param hoverText The text displayed
     * @return this
     */
    public final ChatText setHoverText(String hoverText) {
        if (hoverText != null) {
            handle = handle.setHoverText(ChatText.fromMessage(hoverText).getBackingHandle());
        }
        return this;
    }

    /**
     * Copies the contents of another ChatText and sets it in this object
     * 
     * @param from ChatText to copy from
     * @return this
     */
    public ChatText copy(ChatText from) {
        this.setHandle(from.handle.isMutable() ? from.handle.createCopy() : from.handle);
        return this;
    }

    @Override
    public final ChatText clone() {
        ChatText clone = new ChatText();
        clone.setHandle(this.handle.isMutable() ? this.handle.createCopy() : this.handle);
        return clone;
    }

    /**
     * Creates a chat text component of an empty String
     * 
     * @return empty ChatText
     */
    public static ChatText empty() {
        return fromMessage("");
    }

    /**
     * Creates the text component for representing a clickable link.
     * When clicked, the player is asked to navigate to the URL specified.
     * 
     * @param text The text visible to the player
     * @param url The url navigated to when clicked
     * @return url ChatText
     */
    public static ChatText fromClickableURL(String text, String url) {
        return fromMessage(text).setClickableURL(url);
    }

    /**
     * Creates the text component for representing a clickable link.
     * When clicked, the player is asked to copy the content specified
     * to their clipboard.
     * 
     * @param text The text visible to the player
     * @param content The content to copy when clicked
     * @return content ChatText
     */
    public static ChatText fromClickableContent(String text, String content) {
        return fromMessage(text).setClickableContent(content);
    }

    /**
     * Creates the text component for representing a clickable link.
     * When clicked, it will place the command specified in the
     * chat input box.
     *
     * @param text The text visible to the player
     * @param command The command to suggest
     * @return content ChatText
     */
    public static ChatText fromClickableSuggestedCommand(String text, String command) {
        return fromMessage(text).setClickableSuggestedCommand(command);
    }

    /**
     * Creates the text component for representing a clickable link.
     * When clicked, it will execute the command specified.
     *
     * @param text The text visible to the player
     * @param command The command to run
     * @return content ChatText
     */
    public static ChatText fromClickableRunCommand(String text, String command) {
        return fromMessage(text).setClickableRunCommand(command);
    }

    /**
     * Decodes JSON-encoded chat messages into a Chat Text component
     * 
     * @param jsonText Input json (Mojangson) message data
     * @return ChatText
     */
    public static ChatText fromJson(String jsonText) {
        if (jsonText == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setJson(jsonText);
        return text;
    }

    /**
     * Decodes NBT-encoded chat messages into a Chat Text component
     *
     * @param nbt Input NBT message data
     * @return ChatText
     */
    public static ChatText fromNBT(CommonTag nbt) {
        if (nbt == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setNBT(nbt);
        return text;
    }

    /**
     * Converts a Bukkit-style chat message with Bukkit-style chat formatting characters
     * into a ChatText message.
     * 
     * @param message
     * @return message ChatText
     */
    public static ChatText fromMessage(String message) {
        if (message == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setMessage(message);
        return text;
    }

    /**
     * Wraps the internal representation of chat text data
     * 
     * @param iChatBaseComponentHandle
     * @return ChatText
     */
    public static ChatText fromComponent(Object iChatBaseComponentHandle) {
        if (iChatBaseComponentHandle == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setHandle(IChatBaseComponentHandle.createHandle(iChatBaseComponentHandle));
        return text;
    }

    /**
     * Wraps the chat color code as chat text data
     * 
     * @param color
     * @return ChatText
     */
    public static ChatText fromChatColor(ChatColor color) {
        if (color == null) {
            return null;
        }
        ChatText text = new ChatText();
        text.setHandle(IChatBaseComponentHandle.ChatSerializerHandle.modifiersToComponent(Collections.singleton(color)));
        return text;
    }

    /*
     * This was an old function used to convert text to JSON
     * Does this support all chat styling flags CraftBukkit uses?
     * If so, it would be better to use this function over the current conversion method.
     * Saves creating an IChatBaseComponent in between.
     * TODO: Test this! And test performance!
     */
    protected static String convertTextToJson_old(String text) {
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
