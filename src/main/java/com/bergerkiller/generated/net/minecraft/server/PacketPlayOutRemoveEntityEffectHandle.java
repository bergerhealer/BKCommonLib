package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutRemoveEntityEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutRemoveEntityEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutRemoveEntityEffectClass} */
    public static final PacketPlayOutRemoveEntityEffectClass T = new PacketPlayOutRemoveEntityEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutRemoveEntityEffectHandle.class, "net.minecraft.server.PacketPlayOutRemoveEntityEffect");

    /* ============================================================================== */

    public static PacketPlayOutRemoveEntityEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutRemoveEntityEffectHandle handle = new PacketPlayOutRemoveEntityEffectHandle();
        handle.instance = handleInstance;
        return handle;
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
    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public MobEffectListHandle getEffectList() {
        return T.effectList.get(instance);
    }

    public void setEffectList(MobEffectListHandle value) {
        T.effectList.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutRemoveEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRemoveEntityEffectClass extends Template.Class<PacketPlayOutRemoveEntityEffectHandle> {
        public final Template.Constructor.Converted<PacketPlayOutRemoveEntityEffectHandle> constr = new Template.Constructor.Converted<PacketPlayOutRemoveEntityEffectHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<MobEffectListHandle> effectList = new Template.Field.Converted<MobEffectListHandle>();

    }

}

