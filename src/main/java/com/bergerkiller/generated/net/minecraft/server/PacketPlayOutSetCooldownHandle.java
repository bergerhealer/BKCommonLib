package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Material;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutSetCooldown</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.PacketPlayOutSetCooldown")
public abstract class PacketPlayOutSetCooldownHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSetCooldownClass} */
    public static final PacketPlayOutSetCooldownClass T = Template.Class.create(PacketPlayOutSetCooldownClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSetCooldownHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Material getMaterial();
    public abstract void setMaterial(Material value);
    public abstract int getCooldown();
    public abstract void setCooldown(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutSetCooldown</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSetCooldownClass extends Template.Class<PacketPlayOutSetCooldownHandle> {
        public final Template.Field.Converted<Material> material = new Template.Field.Converted<Material>();
        public final Template.Field.Integer cooldown = new Template.Field.Integer();

    }

}

