package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

public class ChatTextTest {

    @Test
    public void testChatText() {
        String msg = "Hello, " + ChatColor.RED + "World!";
        ChatText text = ChatText.fromMessage(msg);
        assertEquals(msg, text.getMessage());

        Set<String> allowed = new HashSet<String>();
        if (Common.evaluateMCVersion(">=", "1.20.3")) {
            // I dont know what the fuck is going on here, but this is all spigots doing
            // That text: "" really shouldn't be there. Also now literal "Hello, " is without {text:}
            allowed.add("{\"text\":\"\",\"extra\":[\"Hello, \",{\"text\":\"World!\",\"obfuscated\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"color\":\"red\",\"bold\":false}]}");
        }
        if (Common.evaluateMCVersion(">=", "1.16")) {
            // Spigot 1.16 has a (temporary?) bug of including all style modes in the json
            // might get fixed in the future
            allowed.add("{\"extra\":[{\"text\":\"Hello, \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}");
        }
        if (CommonCapabilities.CHAT_TEXT_JSON_VER2) {
            allowed.add("{\"extra\":[{\"text\":\"Hello, \"},{\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}");
        } else {
            allowed.add("{\"extra\":[\"Hello, \",{\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}");
        }
        String result = text.getJson();
        if (!allowed.contains(result)) {
            System.out.println("Incorrect JSON: " + result);
            fail("Chat text conversion to JSON is not working correctly");
        }
    }

    @Test
    public void testSuffixStyle() {
        // Test that a String with a suffix chat style character preserves the style
        String msg = "Prefix" + ChatColor.RED.toString();
        ChatText text = ChatText.fromMessage(msg);
        assertEquals(msg, text.getMessage());
    }

    @Test
    public void testPrefixStyle() {
        // Test that a String with a prefix chat style character preserves the style
        String msg = ChatColor.RED.toString() + "Postfix";
        ChatText text = ChatText.fromMessage(msg);
        assertEquals(msg, text.getMessage());
    }

    @Test
    public void testStyleOnly() {
        String msg = ChatColor.RED.toString();
        ChatText text = ChatText.fromMessage(msg);

        Set<String> allowed = new HashSet<String>();
        allowed.add("{\"extra\":[{\"color\":\"red\",\"text\":\"\"}],\"text\":\"\"}");
        if (Common.evaluateMCVersion(">=", " 1.20.3")) {
            // IDK wtf
            allowed.add("{\"text\":\"\",\"extra\":[{\"text\":\"\",\"obfuscated\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"color\":\"red\",\"bold\":false},{\"text\":\"\",\"color\":\"red\"}]}");
        }
        if (Common.evaluateMCVersion(">=", "1.16")) {
            // For some reason Minecraft repeats the color twice now in the json
            // It's still functionally identical, so we'll allow it I guess.
            allowed.add("{\"extra\":[{\"color\":\"red\",\"text\":\"\"},{\"color\":\"red\",\"text\":\"\"}],\"text\":\"\"}");
            // It's even more messed up now :(
            allowed.add("{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"red\",\"text\":\"\"},{\"color\":\"red\",\"text\":\"\"}],\"text\":\"\"}");
        }
        String json = text.getJson();
        if (!allowed.contains(json)) {
            fail("Invalid JSON: " + json);
        }
        assertEquals(msg, text.getMessage());
    }

    @Test
    public void testFromChatColor() {
        for (ChatColor color : ChatColor.values()) {
            ChatText text = ChatText.fromMessage(color.toString());
            if (color == ChatColor.RESET) {
                assertEquals("", text.getMessage());
            } else {
                assertEquals(color.toString(), text.getMessage());
            }
        }
    }

    @Test
    public void testEmpty() {
        // Test empty chat text
        ChatText text = ChatText.empty();
        assertEquals("", text.getMessage());
        if (Common.evaluateMCVersion(">=", "1.20.3")) {
            // Optimized so it just has a "" as output
            assertEquals("\"\"", text.getJson());
        } else {
            assertEquals("{\"text\":\"\"}", text.getJson());
        }
    }

    @Test
    public void testNewlines() {
        String message = "hello\nworld";
        ChatText text = ChatText.fromMessage(message);
        assertEquals(message, text.getMessage());
    }

    @Test
    @Ignore
    public void testStyleBeforeColor() {
        //TODO: This appears to be a bug in Bukkit itself
        //      Not fixing this for now. Just put color before the styling.
        String message = ChatColor.BOLD.toString() + ChatColor.RED + "test";
        ChatText text = ChatText.fromMessage(message);
        System.out.println(text.getJson());
        assertEquals(message, text.getMessage());
    }

    @Test
    public void testHexColor() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            return; // Skip
        }

        String message = Character.toString(StringUtil.CHAT_STYLE_CHAR) + "x"
                + Character.toString(StringUtil.CHAT_STYLE_CHAR) + "1" + StringUtil.CHAT_STYLE_CHAR + "2"
                + StringUtil.CHAT_STYLE_CHAR + "3" + StringUtil.CHAT_STYLE_CHAR + "4"
                + StringUtil.CHAT_STYLE_CHAR + "a" + StringUtil.CHAT_STYLE_CHAR + "b"
                + "message";
        ChatText text = ChatText.fromMessage(message);
        //System.out.println(text.getJson());
        //System.out.println(message);
        //System.out.println(text.getMessage());
        assertEquals(message, text.getMessage());
    }
}
