package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutSetCooldown</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutSetCooldownHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSetCooldownClass} */
    public static final PacketPlayOutSetCooldownClass T = new PacketPlayOutSetCooldownClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutSetCooldownHandle.class, "net.minecraft.server.PacketPlayOutSetCooldown");

    /* ============================================================================== */

    public static PacketPlayOutSetCooldownHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Material getMaterial() {
        return T.material.get(getRaw());
    }

    public void setMaterial(Material value) {
        T.material.set(getRaw(), value);
    }

    public int getCooldown() {
        return T.cooldown.getInteger(getRaw());
    }

    public void setCooldown(int value) {
        T.cooldown.setInteger(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutSetCooldown</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSetCooldownClass extends Template.Class<PacketPlayOutSetCooldownHandle> {
        public final Template.Field.Converted<Material> material = new Template.Field.Converted<Material>();
        public final Template.Field.Integer cooldown = new Template.Field.Integer();

    }

}

