package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect")
public abstract class PacketPlayOutRemoveEntityEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutRemoveEntityEffectClass} */
    public static final PacketPlayOutRemoveEntityEffectClass T = Template.Class.create(PacketPlayOutRemoveEntityEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutRemoveEntityEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutRemoveEntityEffectHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */


    public static PacketPlayOutRemoveEntityEffectHandle createNew(int entityId, MobEffectListHandle mobEffectList) {
        PacketPlayOutRemoveEntityEffectHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setEffectList(mobEffectList);
        return handle;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract MobEffectListHandle getEffectList();
    public abstract void setEffectList(MobEffectListHandle value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRemoveEntityEffectClass extends Template.Class<PacketPlayOutRemoveEntityEffectHandle> {
        public final Template.Constructor.Converted<PacketPlayOutRemoveEntityEffectHandle> constr = new Template.Constructor.Converted<PacketPlayOutRemoveEntityEffectHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<MobEffectListHandle> effectList = new Template.Field.Converted<MobEffectListHandle>();

    }

}

