package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInTabComplete</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInTabCompleteHandle extends PacketHandle {
    /** @See {@link PacketPlayInTabCompleteClass} */
    public static final PacketPlayInTabCompleteClass T = new PacketPlayInTabCompleteClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInTabCompleteHandle.class, "net.minecraft.server.PacketPlayInTabComplete");

    /* ============================================================================== */

    public static PacketPlayInTabCompleteHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInTabCompleteHandle handle = new PacketPlayInTabCompleteHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public String getText() {
        return T.text.get(instance);
    }

    public void setText(String value) {
        T.text.set(instance, value);
    }

    public IntVector3 getPosition() {
        return T.position.get(instance);
    }

    public void setPosition(IntVector3 value) {
        T.position.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInTabComplete</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInTabCompleteClass extends Template.Class<PacketPlayInTabCompleteHandle> {
        public final Template.Field<String> text = new Template.Field<String>();
        @Template.Optional
        public final Template.Field.Boolean assumeCommand = new Template.Field.Boolean();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();

    }

}

