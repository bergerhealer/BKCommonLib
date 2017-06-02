package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.IChatBaseComponentHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftChatMessageHandle extends Template.Handle {
    public static final CraftChatMessageClass T = new CraftChatMessageClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftChatMessageHandle.class, "org.bukkit.craftbukkit.util.CraftChatMessage");

    /* ============================================================================== */

    public static CraftChatMessageHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftChatMessageHandle handle = new CraftChatMessageHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static String fromComponent(IChatBaseComponentHandle component) {
        return T.fromComponent.invokeVA(component);
    }

    public static IChatBaseComponentHandle[] fromString(String message) {
        return T.fromString.invokeVA(message);
    }

    public static final class CraftChatMessageClass extends Template.Class<CraftChatMessageHandle> {
        public final Template.StaticMethod.Converted<String> fromComponent = new Template.StaticMethod.Converted<String>();
        public final Template.StaticMethod.Converted<IChatBaseComponentHandle[]> fromString = new Template.StaticMethod.Converted<IChatBaseComponentHandle[]>();

    }

}

