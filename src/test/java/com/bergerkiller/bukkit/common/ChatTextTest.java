package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

public class ChatTextTest {

    @Test
    public void testChatText() {
        String msg = "Hello, " + ChatColor.RED + "World!";
        ChatText text = ChatText.fromMessage(msg);
        assertEquals(msg, text.getMessage());

        Set<String> allowed = new HashSet<String>();
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
        assertEquals("{\"text\":\"\"}", text.getJson());
    }
}
