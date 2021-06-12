package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutUpdateAttributes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutUpdateAttributes")
public abstract class PacketPlayOutUpdateAttributesHandle extends PacketHandle {
    /** @See {@link PacketPlayOutUpdateAttributesClass} */
    public static final PacketPlayOutUpdateAttributesClass T = Template.Class.create(PacketPlayOutUpdateAttributesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutUpdateAttributesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutUpdateAttributesHandle createNew(int entityId, Collection<AttributeModifiableHandle> attributes) {
        return T.createNew.invoke(entityId, attributes);
    }

    public static PacketPlayOutUpdateAttributesHandle createZeroMaxHealth(int entityId) {
        return T.createZeroMaxHealth.invoke(entityId);
    }

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutUpdateAttributes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUpdateAttributesClass extends Template.Class<PacketPlayOutUpdateAttributesHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutUpdateAttributesHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutUpdateAttributesHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutUpdateAttributesHandle> createZeroMaxHealth = new Template.StaticMethod.Converted<PacketPlayOutUpdateAttributesHandle>();

    }

}

