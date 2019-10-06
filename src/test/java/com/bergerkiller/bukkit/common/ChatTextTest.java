package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

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

        String expected;
        if (CommonCapabilities.CHAT_TEXT_JSON_VER2) {
            expected = "{\"extra\":[{\"text\":\"Hello, \"},{\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}";
        } else {
            expected = "{\"extra\":[\"Hello, \",{\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}";
        }
        String result = text.getJson();
        if (!expected.equals(result)) {
            System.out.println("EXPECTED: " + expected);
            System.out.println("INSTEAD : " + result);;
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
        assertEquals("{\"extra\":[{\"color\":\"red\",\"text\":\"\"}],\"text\":\"\"}", text.getJson());
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
