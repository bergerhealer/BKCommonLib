package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;

import io.netty.channel.Channel;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class NetworkManagerRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("NetworkManager");
    public static final FieldAccessor<Queue<Object>> lowPriorityQueue = TEMPLATE.getField("k");
    public static final FieldAccessor<Queue<Object>> highPriorityQueue = TEMPLATE.getField("l");
    public static final FieldAccessor<Channel> channel = TEMPLATE.getField("m");
    public static final MethodAccessor<Boolean> getIsOpen = TEMPLATE.getMethod("isConnected");
}
