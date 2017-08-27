package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

public class ChatTextTest {

    static {
        CommonUtil.bootstrap();
    }

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
}
