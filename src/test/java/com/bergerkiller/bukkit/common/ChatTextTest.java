package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.ChatColor;
import org.junit.Test;

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
        assertEquals("{\"extra\":[{\"text\":\"Hello, \"},{\"color\":\"red\",\"text\":\"World!\"}],\"text\":\"\"}",
                text.getJson());
    }
}
